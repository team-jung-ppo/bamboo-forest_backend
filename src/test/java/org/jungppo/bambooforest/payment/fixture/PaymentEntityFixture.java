package org.jungppo.bambooforest.payment.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.jungppo.bambooforest.payment.domain.entity.PaymentStatusType;
import org.jungppo.bambooforest.util.ReflectionUtils;

public class PaymentEntityFixture {

    public static PaymentEntity createPaymentEntity(final UUID id, final PaymentStatusType status,
                                                    final BatteryItem batteryItem,
                                                    final BigDecimal amount, final MemberEntity member) {
        final PaymentEntity paymentEntity = PaymentEntity.of(status, batteryItem, member);
        paymentEntity.updatePaymentDetails("InvalidKey", "Toss", amount);
        ReflectionUtils.setField(paymentEntity, "id", id);
        return paymentEntity;
    }
}
