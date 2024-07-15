package org.jungppo.bambooforest.dto.paymentgateway.toss;

import java.math.BigDecimal;
import java.util.UUID;

import org.jungppo.bambooforest.dto.paymentgateway.PaymentRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentRequest implements PaymentRequest {
	private String paymentKey;
	private UUID orderId;
	private BigDecimal amount;
}
