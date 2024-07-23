package org.jungppo.bambooforest.global.client.paymentgateway.toss.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentRequest;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class TossPaymentRequest implements PaymentRequest {
    private final String paymentKey;
    private final UUID orderId;
    private final BigDecimal amount;
}
