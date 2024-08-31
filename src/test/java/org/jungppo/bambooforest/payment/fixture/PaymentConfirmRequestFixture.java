package org.jungppo.bambooforest.payment.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import org.jungppo.bambooforest.member.dto.PaymentConfirmRequest;

public class PaymentConfirmRequestFixture {

    public static PaymentConfirmRequest createPaymentConfirmRequest(final String paymentKey, final UUID orderId,
                                                                    final BigDecimal amount) {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }
}
