package org.jungppo.bambooforest.dto.paymentgateway.toss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TossFailureResponse {
	private String code;
	private String message;
}
