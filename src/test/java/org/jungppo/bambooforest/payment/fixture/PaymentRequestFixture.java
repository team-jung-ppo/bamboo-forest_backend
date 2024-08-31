package org.jungppo.bambooforest.payment.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.dto.TossPaymentRequest;

public class PaymentRequestFixture {

    public static TossPaymentRequest createTossPaymentRequest(final String paymentKey, final UUID orderId,
                                                              final BigDecimal amount) {
        return new TossPaymentRequest(paymentKey, orderId, amount);
    }
}
