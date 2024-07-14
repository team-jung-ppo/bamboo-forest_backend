package org.jungppo.bambooforest.service.payment;

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
	public PaymentSetupResponse setupPayment(PaymentSetupRequest paymentSetupRequest, Long memberId) {
		BatteryItem batteryItem = BatteryItem.findByName(paymentSetupRequest.getBatteryItemName())
			.orElseThrow(BatteryNotFoundException::new);
		MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

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

		if (!paymentConfirmRequest.getAmount().equals(paymentEntity.getBatteryItem().getPrice())) {
			paymentEntity.updatePaymentStatus(PaymentStatusType.FAILED);
			return new PaymentDto(paymentEntity.getId(), paymentEntity.getStatus(), paymentEntity.getProvider(),
				paymentEntity.getAmount(), paymentEntity.getCreatedAt()
			);
		}

		TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
			paymentConfirmRequest.getPaymentKey(),
			paymentConfirmRequest.getOrderId(),
			paymentConfirmRequest.getAmount()
		);

		paymentGatewayClient.payment(tossPaymentRequest)
			.getData()
			.ifPresentOrElse(successResponse -> {
				paymentEntity.updatePaymentDetails(successResponse.getKey(), successResponse.getProvider(),
					successResponse.getAmount());
				paymentEntity.updatePaymentStatus(PaymentStatusType.COMPLETED);
			}, () -> paymentEntity.updatePaymentStatus(PaymentStatusType.FAILED));

		return new PaymentDto(paymentEntity.getId(), paymentEntity.getStatus(), paymentEntity.getProvider(),
			paymentEntity.getAmount(), paymentEntity.getCreatedAt()
		);
	}
}
