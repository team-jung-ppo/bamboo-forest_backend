package org.jungppo.bambooforest.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.battery.exception.BatteryNotFoundException;
import org.jungppo.bambooforest.global.client.paymentgateway.PaymentGatewayClient;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.dto.TossPaymentRequest;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.PaymentConfirmRequest;
import org.jungppo.bambooforest.member.dto.PaymentDto;
import org.jungppo.bambooforest.member.dto.PaymentSetupRequest;
import org.jungppo.bambooforest.member.dto.PaymentSetupResponse;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.jungppo.bambooforest.payment.domain.entity.PaymentStatusType;
import org.jungppo.bambooforest.payment.domain.repository.PaymentRepository;
import org.jungppo.bambooforest.payment.exception.PaymentFailureException;
import org.jungppo.bambooforest.payment.exception.PaymentNotFoundException;
import org.jungppo.bambooforest.payment.exception.PaymentPendingException;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PaymentGatewayClient paymentGatewayClient;

    @Transactional
    public PaymentSetupResponse setupPayment(final PaymentSetupRequest paymentSetupRequest,
                                             final CustomOAuth2User customOAuth2User) {
        final BatteryItem batteryItem = BatteryItem.findByName(paymentSetupRequest.getBatteryItemName())
                .orElseThrow(BatteryNotFoundException::new);
        final MemberEntity memberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);

        final PaymentEntity paymentEntity = paymentRepository.save(PaymentEntity.builder()
                .batteryItem(batteryItem)
                .status(PaymentStatusType.PENDING)
                .member(memberEntity)
                .build());

        return new PaymentSetupResponse(paymentEntity.getId(), paymentEntity.getBatteryItem().getPrice());
    }

    @Transactional
    public UUID confirmPayment(final PaymentConfirmRequest paymentConfirmRequest,
                               final CustomOAuth2User customOAuth2User) {
        final PaymentEntity paymentEntity = paymentRepository.findById(paymentConfirmRequest.getOrderId())
                .orElseThrow(PaymentNotFoundException::new);

        validatePaymentAmount(paymentConfirmRequest, paymentEntity);
        processPayment(paymentConfirmRequest, paymentEntity, customOAuth2User);

        return paymentEntity.getId();
    }

    private void validatePaymentAmount(final PaymentConfirmRequest request, final PaymentEntity paymentEntity) {
        final BigDecimal requestedAmount = request.getAmount();
        final BigDecimal itemPrice = paymentEntity.getBatteryItem().getPrice();

        if (requestedAmount.compareTo(itemPrice) != 0) {
            throw new PaymentFailureException();
        }
    }

    private void processPayment(final PaymentConfirmRequest request, final PaymentEntity paymentEntity,
                                final CustomOAuth2User customOAuth2User) {
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                request.getPaymentKey(), request.getOrderId(), request.getAmount());

        final PaymentResponse paymentResponse = paymentGatewayClient.payment(tossPaymentRequest)
                .getData()
                .orElseThrow(PaymentFailureException::new);
        final MemberEntity memberEntity = memberRepository.findByIdWithPessimisticLock(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);

        paymentEntity.updatePaymentDetails(paymentResponse.getKey(), paymentResponse.getProvider(),
                paymentResponse.getAmount());
        paymentEntity.updatePaymentStatus(PaymentStatusType.COMPLETED);
        memberEntity.addBatteries(paymentEntity.getBatteryItem().getCount());
    }

    @PreAuthorize(value = "@paymentAccessEvaluator.isEligible(#paymentId, #customOAuth2User.getId())")
    public PaymentDto getPayment(@Param(value = "paymentId") final UUID paymentId,
                                 @Param(value = "customOAuth2User") final CustomOAuth2User customOAuth2User) {
        final PaymentEntity paymentEntity = paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        if (paymentEntity.getStatus() == PaymentStatusType.PENDING) {
            throw new PaymentPendingException();
        }

        return PaymentDto.from(paymentEntity);
    }

    public List<PaymentDto> getPayments(final CustomOAuth2User customOAuth2User) {
        List<PaymentEntity> paymentEntities = paymentRepository.findAllCompletedByMemberIdOrderByCreatedAtDesc(
                customOAuth2User.getId());
        return paymentEntities.stream().map(PaymentDto::from).toList();
    }
}
