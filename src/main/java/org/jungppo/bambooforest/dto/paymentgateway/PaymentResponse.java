package org.jungppo.bambooforest.dto.paymentgateway;

import java.math.BigDecimal;

public interface PaymentResponse {
	String getKey();

	String getProvider();

	BigDecimal getAmount();
}
