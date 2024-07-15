package org.jungppo.bambooforest.global.client.paymentgateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
