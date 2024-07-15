package org.jungppo.bambooforest.global.client.paymentgateway.toss;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.PaymentGatewayClient;
import org.jungppo.bambooforest.global.client.paymentgateway.PaymentGatewayProperties;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentRequest;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.dto.TossPaymentRequest;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.dto.TossSuccessResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableConfigurationProperties(PaymentGatewayProperties.class)
public class TossPaymentGatewayClient implements PaymentGatewayClient {

    private final RestTemplate restTemplate;
    private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final PaymentGatewayProperties paymentGatewayProperties;

    @Override
    public ClientResponse<PaymentResponse> payment(PaymentRequest paymentRequest) {
        TossPaymentRequest tossPaymentRequest = null;
        try {
            tossPaymentRequest = createTossPaymentRequest(paymentRequest);
            HttpEntity<TossPaymentRequest> request = createPaymentRequest(tossPaymentRequest);
            ResponseEntity<TossSuccessResponse> responseEntity = restTemplate.postForEntity(
                    TOSS_PAYMENT_URL,
                    request,
                    TossSuccessResponse.class
            );
            TossSuccessResponse tossPaymentResponse = responseEntity.getBody();
            handleResponse(tossPaymentResponse);
            validateResponse(tossPaymentResponse);
            return ClientResponse.success(tossPaymentResponse);
        } catch (Exception e) {
            log.warn("Failed to process Toss Payment for Order ID: {}",
                    tossPaymentRequest != null ? tossPaymentRequest.getOrderId() : null, e);
            return ClientResponse.failure();
        }
    }

    private TossPaymentRequest createTossPaymentRequest(PaymentRequest paymentRequest) {
        if (!(paymentRequest instanceof TossPaymentRequest)) {
            throw new IllegalArgumentException("Request must be an instance of TossPaymentRequest");
        }
        return (TossPaymentRequest) paymentRequest;
    }

    private HttpEntity<TossPaymentRequest> createPaymentRequest(TossPaymentRequest req) {
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(req, headers);
    }

    private HttpHeaders getHeaders() {
        String authorizations =
                "Basic " + Base64.getEncoder()
                        .encodeToString(
                                (paymentGatewayProperties.getToss().getSecretKey() + ":").getBytes(
                                        StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void handleResponse(TossSuccessResponse response) {
        notNull(response, "Failed to process Toss Response: Response is null");
    }

    private void validateResponse(TossSuccessResponse response) {
        notNull(response.getTotalAmount(), "Total amount must not be null");
        if (response.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Total amount must be greater than zero");
        }
        if (response.getKey() == null || response.getKey().isEmpty()) {
            throw new IllegalStateException("Response key cannot be null or empty");
        }
    }
}
