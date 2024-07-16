package org.jungppo.bambooforest.global.client.paymentgateway;

import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentRequest;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentResponse;

public interface PaymentGatewayClient {
    ClientResponse<PaymentResponse> payment(PaymentRequest paymentRequest);
}
