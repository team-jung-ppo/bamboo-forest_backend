package org.jungppo.bambooforest.global.client.paymentgateway.dto;

import java.math.BigDecimal;

public interface PaymentResponse {
    String getKey();

    String getProvider();

    BigDecimal getAmount();
}
