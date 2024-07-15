package org.jungppo.bambooforest.client.paymentgateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "paymentgateway")
public class PaymentGatewayProperties {
	private Toss toss;

	@Getter
	@Setter
	public static class Toss {
		private String secretKey;
	}
}
