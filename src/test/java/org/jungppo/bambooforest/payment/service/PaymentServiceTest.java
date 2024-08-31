package org.jungppo.bambooforest.payment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jungppo.bambooforest.global.oauth2.fixture.CustomOAuth2UserFixture.CUSTOM_OAUTH2_USER;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.MEMBER_ENTITY;
import static org.jungppo.bambooforest.payment.fixture.PaymentConfirmRequestFixture.MEDIUM_BATTERY_PAYMENT_CONFIRM_REQUEST;
import static org.jungppo.bambooforest.payment.fixture.PaymentConfirmRequestFixture.SMALL_BATTERY_PAYMENT_CONFIRM_REQUEST;
import static org.jungppo.bambooforest.payment.fixture.PaymentEntityFixture.MEDIUM_BATTERY_PAYMENT_ENTITY_COMPLETED;
import static org.jungppo.bambooforest.payment.fixture.PaymentEntityFixture.MEDIUM_BATTERY_PAYMENT_ENTITY_PENDING;
import static org.jungppo.bambooforest.payment.fixture.PaymentEntityFixture.SMALL_BATTERY_PAYMENT_ENTITY_PENDING;
import static org.jungppo.bambooforest.payment.fixture.PaymentRequestFixture.SMALL_BATTERY_PAYMENT_REQUEST;
import static org.jungppo.bambooforest.payment.fixture.PaymentResponseFixture.SMALL_BATTERY_PAYMENT_SUCCESS_RESPONSE;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupRequestFixture.INVALID_BATTERY_SETUP_REQUEST;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupRequestFixture.SMALL_BATTERY_SETUP_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jungppo.bambooforest.battery.exception.BatteryNotFoundException;
import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentRequest;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.TossPaymentGatewayClient;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.PaymentConfirmRequest;
import org.jungppo.bambooforest.member.dto.PaymentDto;
import org.jungppo.bambooforest.member.dto.PaymentSetupRequest;
import org.jungppo.bambooforest.member.dto.PaymentSetupResponse;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.jungppo.bambooforest.payment.domain.entity.PaymentStatusType;
import org.jungppo.bambooforest.payment.domain.repository.PaymentRepository;
import org.jungppo.bambooforest.payment.exception.PaymentFailureException;
import org.jungppo.bambooforest.payment.exception.PaymentNotFoundException;
import org.jungppo.bambooforest.payment.exception.PaymentPendingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TossPaymentGatewayClient tossPaymentGatewayClient;

    @Test
    void testSetupPayment() {
        // given
        PaymentSetupRequest paymentSetupRequest = SMALL_BATTERY_SETUP_REQUEST;
        MemberEntity memberEntity = MEMBER_ENTITY;
        PaymentEntity paymentEntity = SMALL_BATTERY_PAYMENT_ENTITY_PENDING;

        when(memberRepository.findById(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(Optional.of(memberEntity));
        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(paymentEntity);

        // when
        final PaymentSetupResponse response = paymentService.setupPayment(paymentSetupRequest, CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(response.getOrderId()).isEqualTo(paymentEntity.getId());
            softly.assertThat(response.getAmount()).isEqualTo(paymentEntity.getBatteryItem().getPrice());
            verify(memberRepository).findById(eq(CUSTOM_OAUTH2_USER.getId()));
            verify(paymentRepository).save(any(PaymentEntity.class));
        });
    }

    @Test
    void testSetupPayment_BatteryNotFound() {
        // given
        PaymentSetupRequest paymentSetupRequest = INVALID_BATTERY_SETUP_REQUEST;

        // when & then
        assertThatThrownBy(() -> paymentService.setupPayment(paymentSetupRequest, CUSTOM_OAUTH2_USER))
                .isInstanceOf(BatteryNotFoundException.class);
    }

    @Test
    void testConfirmPayment() {
        // given
        PaymentConfirmRequest paymentConfirmRequest = SMALL_BATTERY_PAYMENT_CONFIRM_REQUEST;
        MemberEntity memberEntity = MEMBER_ENTITY;
        PaymentEntity paymentEntity = SMALL_BATTERY_PAYMENT_ENTITY_PENDING;
        PaymentRequest paymentRequest = SMALL_BATTERY_PAYMENT_REQUEST;
        PaymentResponse paymentResponse = SMALL_BATTERY_PAYMENT_SUCCESS_RESPONSE;

        when(paymentRepository.findById(eq(paymentConfirmRequest.getOrderId())))
                .thenReturn(Optional.of(paymentEntity));
        when(tossPaymentGatewayClient.payment(eq(paymentRequest)))
                .thenReturn(ClientResponse.success(paymentResponse));
        when(memberRepository.findByIdWithPessimisticLock(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(Optional.of(memberEntity));

        // when
        final UUID confirmedPaymentId = paymentService.confirmPayment(paymentConfirmRequest, CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(confirmedPaymentId).isEqualTo(paymentEntity.getId());
            softly.assertThat(paymentEntity.getStatus()).isEqualTo(PaymentStatusType.COMPLETED);
            softly.assertThat(paymentEntity.getKey()).isEqualTo(paymentResponse.getKey());
            softly.assertThat(paymentEntity.getProvider()).isEqualTo(paymentResponse.getProvider());
            softly.assertThat(paymentEntity.getAmount()).isEqualTo(paymentResponse.getAmount());
            verify(paymentRepository).findById(eq(paymentConfirmRequest.getOrderId()));
            verify(tossPaymentGatewayClient).payment(eq(paymentRequest));
            verify(memberRepository).findByIdWithPessimisticLock(eq(CUSTOM_OAUTH2_USER.getId()));
        });
    }

    @Test
    void testConfirmPayment_InvalidAmount() {
        // given
        PaymentConfirmRequest paymentConfirmRequest = MEDIUM_BATTERY_PAYMENT_CONFIRM_REQUEST;
        PaymentEntity paymentEntity = SMALL_BATTERY_PAYMENT_ENTITY_PENDING;

        when(paymentRepository.findById(eq(paymentConfirmRequest.getOrderId())))
                .thenReturn(Optional.of(paymentEntity));

        // when & then
        assertThatThrownBy(() -> paymentService.confirmPayment(paymentConfirmRequest, CUSTOM_OAUTH2_USER))
                .isInstanceOf(PaymentFailureException.class);
    }

    @Test
    void testGetPayment() {
        // given
        PaymentEntity paymentEntity = MEDIUM_BATTERY_PAYMENT_ENTITY_COMPLETED;

        when(paymentRepository.findById(eq(MEDIUM_BATTERY_PAYMENT_ENTITY_COMPLETED.getId())))
                .thenReturn(Optional.of(paymentEntity));

        // when
        final PaymentDto paymentDto = paymentService.getPayment(MEDIUM_BATTERY_PAYMENT_ENTITY_COMPLETED.getId(),
                CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(paymentDto.getId()).isEqualTo(paymentEntity.getId());
            verify(paymentRepository).findById(eq(MEDIUM_BATTERY_PAYMENT_ENTITY_COMPLETED.getId()));
        });
    }

    @Test
    void testGetPayment_PaymentNotFound() {
        // given
        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(eq(paymentId)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPayment(paymentId, CUSTOM_OAUTH2_USER))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void testGetPayment_PaymentPending() {
        // given
        PaymentEntity paymentEntity = MEDIUM_BATTERY_PAYMENT_ENTITY_PENDING;

        when(paymentRepository.findById(eq(MEDIUM_BATTERY_PAYMENT_ENTITY_PENDING.getId())))
                .thenReturn(Optional.of(paymentEntity));

        // when & then
        assertThatThrownBy(
                () -> paymentService.getPayment(MEDIUM_BATTERY_PAYMENT_ENTITY_PENDING.getId(), CUSTOM_OAUTH2_USER))
                .isInstanceOf(PaymentPendingException.class);
    }

    @Test
    void testGetPayments() {
        // given
        PaymentEntity paymentEntity = MEDIUM_BATTERY_PAYMENT_ENTITY_COMPLETED;

        when(paymentRepository.findAllCompletedByMemberIdOrderByCreatedAtDesc(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(List.of(paymentEntity));

        // when
        final List<PaymentDto> payments = paymentService.getPayments(CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(payments).hasSize(1);
            softly.assertThat(payments.get(0).getId()).isEqualTo(paymentEntity.getId());
            verify(paymentRepository).findAllCompletedByMemberIdOrderByCreatedAtDesc(eq(CUSTOM_OAUTH2_USER.getId()));
        });
    }
}
