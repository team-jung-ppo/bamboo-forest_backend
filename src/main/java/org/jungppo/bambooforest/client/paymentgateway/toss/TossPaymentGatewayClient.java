package org.jungppo.bambooforest.client.paymentgateway.toss;

import static org.springframework.util.Assert.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.jungppo.bambooforest.client.ClientResponse;
import org.jungppo.bambooforest.client.paymentgateway.PaymentGatewayClient;
import org.jungppo.bambooforest.client.paymentgateway.PaymentGatewayProperties;
import org.jungppo.bambooforest.dto.paymentgateway.PaymentRequest;
import org.jungppo.bambooforest.dto.paymentgateway.PaymentResponse;
import org.jungppo.bambooforest.dto.paymentgateway.toss.TossPaymentRequest;
import org.jungppo.bambooforest.dto.paymentgateway.toss.TossSuccessResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
			ResponseEntity<TossSuccessResponse> responseEntity = restTemplate.exchange(
				TOSS_PAYMENT_URL,
				HttpMethod.POST,
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
		return (TossPaymentRequest)paymentRequest;
	}

	private HttpEntity<TossPaymentRequest> createPaymentRequest(TossPaymentRequest req) {
		HttpHeaders headers = getHeaders();
		return new HttpEntity<>(req, headers);
	}

	private HttpHeaders getHeaders() {
		String authorizations =
			"Basic " + Base64.getEncoder()
				.encodeToString(
					(paymentGatewayProperties.getToss().getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
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
