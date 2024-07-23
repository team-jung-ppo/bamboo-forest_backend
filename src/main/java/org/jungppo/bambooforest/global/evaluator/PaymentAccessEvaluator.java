package org.jungppo.bambooforest.global.evaluator;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.jungppo.bambooforest.payment.domain.repository.PaymentRepository;
import org.jungppo.bambooforest.payment.exception.PaymentNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentAccessEvaluator implements Evaluator<UUID> {

    private final PaymentRepository paymentRepository;

    @Override
    public boolean isEligible(final UUID targetId, final Long memberId) {
        final PaymentEntity paymentEntity = paymentRepository.findById(targetId)
                .orElseThrow(PaymentNotFoundException::new);
        return paymentEntity.getMember().getId().equals(memberId);
    }
}
