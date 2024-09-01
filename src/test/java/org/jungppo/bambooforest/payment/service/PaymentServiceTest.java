package org.jungppo.bambooforest.payment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jungppo.bambooforest.battery.domain.BatteryItem.MEDIUM_BATTERY;
import static org.jungppo.bambooforest.battery.domain.BatteryItem.SMALL_BATTERY;
import static org.jungppo.bambooforest.global.oauth2.fixture.CustomOAuth2UserFixture.createCustomOAuth2User;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.createMemberEntity;
import static org.jungppo.bambooforest.payment.domain.entity.PaymentStatusType.COMPLETED;
import static org.jungppo.bambooforest.payment.fixture.PaymentConfirmRequestFixture.createPaymentConfirmRequest;
import static org.jungppo.bambooforest.payment.fixture.PaymentEntityFixture.createPaymentEntity;
import static org.jungppo.bambooforest.payment.fixture.PaymentRequestFixture.createTossPaymentRequest;
import static org.jungppo.bambooforest.payment.fixture.PaymentResponseFixture.createPaymentSuccessResponse;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupRequestFixture.createPaymentSetupRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.battery.dto.BatteryItemDto;
import org.jungppo.bambooforest.battery.exception.BatteryNotFoundException;
import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentRequest;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.TossPaymentGatewayClient;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
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
import org.junit.jupiter.api.BeforeEach;
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

    private MemberEntity memberEntity;
    private CustomOAuth2User customOAuth2User;

    @BeforeEach
    void setUp() {
        memberEntity = createMemberEntity(1L, null, "username", "profileImageUrl", null);
        customOAuth2User = createCustomOAuth2User(memberEntity.getId(), memberEntity.getRole(),
                memberEntity.getOAuth2());
    }

    @Test
    void testSetupPayment() {
        // given
        PaymentSetupRequest paymentSetupRequest = createPaymentSetupRequest(SMALL_BATTERY.getName());
        PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), PaymentStatusType.PENDING, SMALL_BATTERY,
                null, memberEntity);

        given(memberRepository.findById(eq(customOAuth2User.getId())))
                .willReturn(Optional.of(memberEntity));
        given(paymentRepository.save(any(PaymentEntity.class)))
                .willReturn(paymentEntity);

        // when
        PaymentSetupResponse response = paymentService.setupPayment(paymentSetupRequest, customOAuth2User);

        // then
        assertSoftly(softly -> {
            softly.assertThat(response.getOrderId()).isEqualTo(paymentEntity.getId());
            softly.assertThat(response.getAmount()).isEqualTo(paymentEntity.getBatteryItem().getPrice());
        });
    }

    @Test
    void testSetupPayment_BatteryNotFound() {
        // given
        PaymentSetupRequest paymentSetupRequest = createPaymentSetupRequest("InvalidBatteryItem");

        // when & then
        assertThatThrownBy(() -> paymentService.setupPayment(paymentSetupRequest, customOAuth2User))
                .isInstanceOf(BatteryNotFoundException.class);
    }

    @Test
    void testConfirmPayment() {
        // given
        PaymentConfirmRequest paymentConfirmRequest = createPaymentConfirmRequest("validPaymentKey",
                UUID.randomUUID(), SMALL_BATTERY.getPrice());
        PaymentEntity paymentEntity = createPaymentEntity(paymentConfirmRequest.getOrderId(), PaymentStatusType.PENDING,
                SMALL_BATTERY, null, memberEntity);

        PaymentRequest paymentRequest = createTossPaymentRequest(paymentConfirmRequest.getPaymentKey(),
                paymentConfirmRequest.getOrderId(), paymentConfirmRequest.getAmount());
        PaymentResponse paymentResponse = createPaymentSuccessResponse(SMALL_BATTERY);

        given(paymentRepository.findById(eq(paymentConfirmRequest.getOrderId())))
                .willReturn(Optional.of(paymentEntity));
        given(tossPaymentGatewayClient.payment(eq(paymentRequest)))
                .willReturn(ClientResponse.success(paymentResponse));
        given(memberRepository.findByIdWithPessimisticLock(eq(memberEntity.getId())))
                .willReturn(Optional.of(memberEntity));

        // when
        UUID confirmedPaymentId = paymentService.confirmPayment(paymentConfirmRequest, customOAuth2User);

        // then
        assertSoftly(softly -> {
            softly.assertThat(confirmedPaymentId).isEqualTo(paymentEntity.getId());
            softly.assertThat(paymentEntity.getStatus()).isEqualTo(COMPLETED);
            softly.assertThat(memberEntity.getBatteryCount()).isEqualTo(paymentEntity.getBatteryItem().getCount());
        });
    }

    @Test
    void testConfirmPayment_InvalidAmount() {
        // given
        PaymentConfirmRequest paymentConfirmRequest = createPaymentConfirmRequest("validPaymentKey",
                UUID.randomUUID(), SMALL_BATTERY.getPrice());
        PaymentEntity paymentEntity = createPaymentEntity(paymentConfirmRequest.getOrderId(),
                PaymentStatusType.PENDING, MEDIUM_BATTERY, null, memberEntity);

        given(paymentRepository.findById(eq(paymentConfirmRequest.getOrderId())))
                .willReturn(Optional.of(paymentEntity));

        // when & then
        assertThatThrownBy(() -> paymentService.confirmPayment(paymentConfirmRequest, customOAuth2User))
                .isInstanceOf(PaymentFailureException.class);
    }

    @Test
    void testGetPayment() {
        // given
        PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), COMPLETED, SMALL_BATTERY,
                SMALL_BATTERY.getPrice(), memberEntity);

        given(paymentRepository.findById(eq(paymentEntity.getId())))
                .willReturn(Optional.of(paymentEntity));

        // when
        PaymentDto paymentDto = paymentService.getPayment(paymentEntity.getId(), customOAuth2User);

        // then
        assertThat(paymentDto)
                .usingRecursiveComparison()
                .withComparatorForFields(
                        (dtoItem, entityItem) -> ((BatteryItemDto) dtoItem).getName()
                                .compareTo(((BatteryItem) entityItem).getName()),
                        "batteryItem")
                .isEqualTo(paymentEntity);
    }

    @Test
    void testGetPayment_PaymentNotFound() {
        // given
        UUID paymentId = UUID.randomUUID();

        given(paymentRepository.findById(eq(paymentId)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPayment(paymentId, customOAuth2User))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void testGetPayment_PaymentPending() {
        // given
        PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), PaymentStatusType.PENDING, SMALL_BATTERY,
                null, memberEntity);

        given(paymentRepository.findById(eq(paymentEntity.getId())))
                .willReturn(Optional.of(paymentEntity));

        // when & then
        assertThatThrownBy(() -> paymentService.getPayment(paymentEntity.getId(), customOAuth2User))
                .isInstanceOf(PaymentPendingException.class);
    }

    @Test
    void testGetPayments() {
        // given
        PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), COMPLETED, SMALL_BATTERY, null,
                memberEntity);

        given(paymentRepository.findAllCompletedByMemberIdOrderByCreatedAtDesc(eq(customOAuth2User.getId())))
                .willReturn(List.of(paymentEntity));

        // when
        List<PaymentDto> payments = paymentService.getPayments(customOAuth2User);

        // then
        assertThat(payments)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(PaymentDto.from(paymentEntity));
    }
}
