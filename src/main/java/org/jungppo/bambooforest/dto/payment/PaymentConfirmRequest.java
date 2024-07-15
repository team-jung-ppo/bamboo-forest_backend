package org.jungppo.bambooforest.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmRequest {

	@NotBlank(message = "Payment key cannot be blank")
	private String paymentKey;

	@NotNull(message = "Order ID cannot be null")
	private UUID orderId;

	@NotNull(message = "Amount cannot be null")
	@Min(value = 1, message = "Amount must be greater than zero")
	private BigDecimal amount;
}
