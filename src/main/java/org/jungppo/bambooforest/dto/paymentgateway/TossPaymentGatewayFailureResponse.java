package org.jungppo.bambooforest.dto.paymentgateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TossPaymentGatewayFailureResponse {
	private String code;
	private String message;
}
