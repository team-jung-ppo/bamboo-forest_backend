package org.jungppo.bambooforest.global.client.paymentgateway.toss.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentRequest implements PaymentRequest {
    private String paymentKey;
    private UUID orderId;
    private BigDecimal amount;
}
