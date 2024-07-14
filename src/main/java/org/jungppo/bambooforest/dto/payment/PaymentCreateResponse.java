package org.jungppo.bambooforest.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateResponse {
	private UUID paymentId;
	private BigDecimal price;
}
