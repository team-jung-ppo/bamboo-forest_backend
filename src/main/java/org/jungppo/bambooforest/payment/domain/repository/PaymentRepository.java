package org.jungppo.bambooforest.payment.domain.repository;

import java.util.UUID;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
