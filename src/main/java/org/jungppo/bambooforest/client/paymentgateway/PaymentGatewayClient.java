package org.jungppo.bambooforest.client.paymentgateway;

import org.jungppo.bambooforest.client.ClientResponse;
import org.jungppo.bambooforest.dto.paymentgateway.PaymentRequest;
import org.jungppo.bambooforest.dto.paymentgateway.PaymentResponse;

public interface PaymentGatewayClient {
	ClientResponse<PaymentResponse> payment(PaymentRequest paymentRequest);
}
