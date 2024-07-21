package org.jungppo.bambooforest.payment.domain.repository;

import java.util.List;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;

public interface QuerydslPaymentRepository {
    List<PaymentEntity> findAllCompletedByMemberIdOrderByCreatedAtDesc(Long memberId);
}
