package org.jungppo.bambooforest.payment.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import org.jungppo.bambooforest.member.dto.PaymentSetupResponse;

public class PaymentSetupResponseFixture {

    public static PaymentSetupResponse createPaymentSetupResponse(final UUID orderId, final BigDecimal price) {
        return new PaymentSetupResponse(orderId, price);
    }
}
