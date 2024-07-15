package org.jungppo.bambooforest.controller.payment;

import static org.springframework.http.HttpStatus.*;

import org.jungppo.bambooforest.dto.payment.PaymentConfirmRequest;
import org.jungppo.bambooforest.dto.payment.PaymentDto;
import org.jungppo.bambooforest.dto.payment.PaymentSetupRequest;
import org.jungppo.bambooforest.dto.payment.PaymentSetupResponse;
import org.jungppo.bambooforest.security.oauth2.CustomOAuth2User;
import org.jungppo.bambooforest.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/setup")
	public ResponseEntity<PaymentSetupResponse> setupPayment(
		@Valid @RequestBody PaymentSetupRequest paymentSetupRequest, @AuthenticationPrincipal
	CustomOAuth2User customOAuth2User) {
		final PaymentSetupResponse paymentSetupResponse = paymentService.setupPayment(paymentSetupRequest,
			customOAuth2User);
		return ResponseEntity.status(CREATED).body(paymentSetupResponse);
	}

	@PostMapping("/confirm")
	public ResponseEntity<PaymentDto> confirmPayment(
		@Valid @RequestBody PaymentConfirmRequest paymentConfirmRequest) {
		PaymentDto paymentDto = paymentService.confirmPayment(paymentConfirmRequest);
		return ResponseEntity.ok().body(paymentDto);
	}
}
