package org.jungppo.bambooforest.repository.payment;

import java.util.UUID;

import org.jungppo.bambooforest.entity.payment.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
