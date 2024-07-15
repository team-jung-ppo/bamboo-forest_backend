package org.jungppo.bambooforest.service.payment;

import java.math.BigDecimal;

import org.jungppo.bambooforest.client.paymentgateway.PaymentGatewayClient;
import org.jungppo.bambooforest.dto.payment.PaymentConfirmRequest;
import org.jungppo.bambooforest.dto.payment.PaymentDto;
import org.jungppo.bambooforest.dto.payment.PaymentSetupRequest;
import org.jungppo.bambooforest.dto.payment.PaymentSetupResponse;
import org.jungppo.bambooforest.dto.paymentgateway.toss.TossPaymentRequest;
import org.jungppo.bambooforest.entity.battery.BatteryItem;
import org.jungppo.bambooforest.entity.member.MemberEntity;
import org.jungppo.bambooforest.entity.payment.PaymentEntity;
import org.jungppo.bambooforest.entity.type.PaymentStatusType;
import org.jungppo.bambooforest.repository.member.MemberRepository;
import org.jungppo.bambooforest.repository.payment.PaymentRepository;
import org.jungppo.bambooforest.response.exception.battery.BatteryNotFoundException;
import org.jungppo.bambooforest.response.exception.member.MemberNotFoundException;
import org.jungppo.bambooforest.response.exception.payment.PaymentNotFoundException;
import org.jungppo.bambooforest.security.oauth2.CustomOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final MemberRepository memberRepository;
	private final PaymentGatewayClient paymentGatewayClient;

	@Transactional
	public PaymentSetupResponse setupPayment(PaymentSetupRequest paymentSetupRequest,
		CustomOAuth2User customOAuth2User) {
		BatteryItem batteryItem = BatteryItem.findByName(paymentSetupRequest.getBatteryItemName())
			.orElseThrow(BatteryNotFoundException::new);
		MemberEntity memberEntity = memberRepository.findById(customOAuth2User.getId())
			.orElseThrow(MemberNotFoundException::new);

		PaymentEntity paymentEntity = paymentRepository.save(PaymentEntity.builder()
			.batteryItem(batteryItem)
			.status(PaymentStatusType.PENDING)
			.member(memberEntity)
			.build());

		return new PaymentSetupResponse(paymentEntity.getId(), paymentEntity.getBatteryItem().getPrice());
	}

	@Transactional
	public PaymentDto confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
		PaymentEntity paymentEntity = paymentRepository.findById(paymentConfirmRequest.getOrderId())
			.orElseThrow(PaymentNotFoundException::new);

		if (!validatePaymentAmount(paymentConfirmRequest, paymentEntity))
			return new PaymentDto(paymentEntity.getId(), paymentEntity.getStatus(), paymentEntity.getProvider(),
				paymentEntity.getAmount(), paymentEntity.getCreatedAt()
			);

		processPayment(paymentConfirmRequest, paymentEntity);

		return new PaymentDto(paymentEntity.getId(), paymentEntity.getStatus(), paymentEntity.getProvider(),
			paymentEntity.getAmount(), paymentEntity.getCreatedAt()
		);
	}

	private boolean validatePaymentAmount(PaymentConfirmRequest request, PaymentEntity paymentEntity) {
		BigDecimal requestedAmount = request.getAmount();
		BigDecimal itemPrice = paymentEntity.getBatteryItem().getPrice();

		if (requestedAmount.compareTo(itemPrice) != 0) {
			paymentEntity.updatePaymentStatus(PaymentStatusType.FAILED);
			return false;
		}
		return true;
	}

	private void processPayment(PaymentConfirmRequest request, PaymentEntity paymentEntity) {
		TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
			request.getPaymentKey(), request.getOrderId(), request.getAmount());

		paymentGatewayClient.payment(tossPaymentRequest)
			.getData()
			.ifPresentOrElse(successResponse -> {
				paymentEntity.updatePaymentDetails(successResponse.getKey(), successResponse.getProvider(),
					successResponse.getAmount());
				paymentEntity.updatePaymentStatus(PaymentStatusType.COMPLETED);
				paymentEntity.getMember().addBatteries(paymentEntity.getBatteryItem().getCount());
			}, () -> paymentEntity.updatePaymentStatus(PaymentStatusType.FAILED));
	}
}
