package org.jungppo.bambooforest.payment.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.dto.PaymentConfirmRequest;
import org.jungppo.bambooforest.member.dto.PaymentDto;
import org.jungppo.bambooforest.member.dto.PaymentSetupRequest;
import org.jungppo.bambooforest.member.dto.PaymentSetupResponse;
import org.jungppo.bambooforest.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/setup")
    public ResponseEntity<PaymentSetupResponse> setupPayment(
            @Valid @RequestBody final PaymentSetupRequest paymentSetupRequest,
            @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        final PaymentSetupResponse paymentSetupResponse = paymentService.setupPayment(paymentSetupRequest,
                customOAuth2User);
        return ResponseEntity.ok().body(paymentSetupResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentDto> confirmPayment(
            @Valid @RequestBody final PaymentConfirmRequest paymentConfirmRequest,
            @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        final UUID paymentId = paymentService.confirmPayment(paymentConfirmRequest, customOAuth2User);
        return ResponseEntity.created(URI.create("/api/payments/" + paymentId)).build();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPayment(
            @PathVariable(name = "paymentId") final UUID paymentId,
            @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        final PaymentDto paymentDto = paymentService.getPayment(paymentId, customOAuth2User);
        return ResponseEntity.ok().body(paymentDto);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments(
            @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        final List<PaymentDto> paymentDtos = paymentService.getPayments(customOAuth2User);
        return ResponseEntity.ok().body(paymentDtos);
    }
}
