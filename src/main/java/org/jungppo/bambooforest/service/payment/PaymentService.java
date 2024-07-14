package org.jungppo.bambooforest.service.payment;

import org.jungppo.bambooforest.dto.payment.PaymentCreateRequest;
import org.jungppo.bambooforest.dto.payment.PaymentCreateResponse;
import org.jungppo.bambooforest.entity.battery.BatteryItem;
import org.jungppo.bambooforest.entity.member.MemberEntity;
import org.jungppo.bambooforest.entity.payment.PaymentEntity;
import org.jungppo.bambooforest.entity.type.PaymentStatusType;
import org.jungppo.bambooforest.repository.member.MemberRepository;
import org.jungppo.bambooforest.repository.payment.PaymentRepository;
import org.jungppo.bambooforest.response.exception.member.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public PaymentCreateResponse setupPayment(PaymentCreateRequest paymentCreateRequest, Long memberId) {
		BatteryItem batteryItem = BatteryItem.findByName(paymentCreateRequest.getBatteryItemName()).orElseThrow();
		MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
		PaymentEntity paymentEntity = paymentRepository.save(PaymentEntity.builder()
			.batteryItem(batteryItem)
			.status(PaymentStatusType.PENDING)
			.member(memberEntity)
			.build());
		return new PaymentCreateResponse(paymentEntity.getId(), paymentEntity.getBatteryItem().getPrice());
	}
}
