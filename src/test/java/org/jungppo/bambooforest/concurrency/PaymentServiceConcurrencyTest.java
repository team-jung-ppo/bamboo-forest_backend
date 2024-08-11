package org.jungppo.bambooforest.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.paymentgateway.PaymentGatewayClient;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.dto.TossPaymentRequest;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.dto.TossPaymentSuccessResponse;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.PaymentConfirmRequest;
import org.jungppo.bambooforest.member.dto.PaymentSetupRequest;
import org.jungppo.bambooforest.member.dto.PaymentSetupResponse;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.payment.service.PaymentService;
import org.jungppo.bambooforest.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceConcurrencyTest {

    @MockBean
    private PaymentGatewayClient paymentGatewayClient;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private CustomOAuth2User customOAuth2User;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();

        final MemberEntity memberEntity = MemberEntity.of("testUser", OAuth2Type.OAUTH2_GITHUB, "testUser",
                "testProfileImage", RoleType.ROLE_USER);

        final MemberEntity savedMemberEntity = memberRepository.save(memberEntity);

        customOAuth2User = new CustomOAuth2User(savedMemberEntity.getId(), savedMemberEntity.getRole(),
                savedMemberEntity.getOAuth2());
    }

    @Test
    void testSequentialPaymentRequests() {
        final PaymentSetupRequest setupRequest1 = new PaymentSetupRequest(BatteryItem.SMALL_BATTERY.getName());
        final PaymentSetupResponse setupResponse1 = paymentService.setupPayment(setupRequest1, customOAuth2User);

        final PaymentSetupRequest setupRequest2 = new PaymentSetupRequest(BatteryItem.MEDIUM_BATTERY.getName());
        final PaymentSetupResponse setupResponse2 = paymentService.setupPayment(setupRequest2, customOAuth2User);

        final PaymentSetupRequest setupRequest3 = new PaymentSetupRequest(BatteryItem.LARGE_BATTERY.getName());
        final PaymentSetupResponse setupResponse3 = paymentService.setupPayment(setupRequest3, customOAuth2User);

        final PaymentConfirmRequest confirmRequest1 = new PaymentConfirmRequest(
                "testPaymentKey1", setupResponse1.getOrderId(), setupResponse1.getAmount());

        final PaymentConfirmRequest confirmRequest2 = new PaymentConfirmRequest(
                "testPaymentKey2", setupResponse2.getOrderId(), setupResponse2.getAmount());

        final PaymentConfirmRequest confirmRequest3 = new PaymentConfirmRequest(
                "testPaymentKey3", setupResponse3.getOrderId(), setupResponse3.getAmount());

        simulatePaymentSuccess(confirmRequest1.getPaymentKey(), confirmRequest1.getAmount(),
                confirmRequest1.getOrderId());
        simulatePaymentSuccess(confirmRequest2.getPaymentKey(), confirmRequest2.getAmount(),
                confirmRequest2.getOrderId());
        simulatePaymentSuccess(confirmRequest3.getPaymentKey(), confirmRequest3.getAmount(),
                confirmRequest3.getOrderId());

        try {
            paymentService.confirmPayment(confirmRequest1, customOAuth2User);
            paymentService.confirmPayment(confirmRequest2, customOAuth2User);
            paymentService.confirmPayment(confirmRequest3, customOAuth2User);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }

        final MemberEntity updatedMemberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);

        assertEquals(18, updatedMemberEntity.getBatteryCount(), "Remaining batteries should be 18");
    }

    @Test
    void testConcurrentPaymentRequests() throws Exception {
        final int numberOfThreads = 3;
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        final PaymentSetupRequest setupRequest1 = new PaymentSetupRequest(BatteryItem.SMALL_BATTERY.getName());
        final PaymentSetupResponse setupResponse1 = paymentService.setupPayment(setupRequest1, customOAuth2User);

        final PaymentSetupRequest setupRequest2 = new PaymentSetupRequest(BatteryItem.MEDIUM_BATTERY.getName());
        final PaymentSetupResponse setupResponse2 = paymentService.setupPayment(setupRequest2, customOAuth2User);

        final PaymentSetupRequest setupRequest3 = new PaymentSetupRequest(BatteryItem.LARGE_BATTERY.getName());
        final PaymentSetupResponse setupResponse3 = paymentService.setupPayment(setupRequest3, customOAuth2User);

        final PaymentConfirmRequest confirmRequest1 = new PaymentConfirmRequest(
                "testPaymentKey1", setupResponse1.getOrderId(), setupResponse1.getAmount());

        final PaymentConfirmRequest confirmRequest2 = new PaymentConfirmRequest(
                "testPaymentKey2", setupResponse2.getOrderId(), setupResponse2.getAmount());

        final PaymentConfirmRequest confirmRequest3 = new PaymentConfirmRequest(
                "testPaymentKey3", setupResponse3.getOrderId(), setupResponse3.getAmount());

        simulatePaymentSuccess(confirmRequest1.getPaymentKey(), confirmRequest1.getAmount(),
                confirmRequest1.getOrderId());
        simulatePaymentSuccess(confirmRequest2.getPaymentKey(), confirmRequest2.getAmount(),
                confirmRequest2.getOrderId());
        simulatePaymentSuccess(confirmRequest3.getPaymentKey(), confirmRequest3.getAmount(),
                confirmRequest3.getOrderId());

        executorService.submit(() -> {
            try {
                paymentService.confirmPayment(confirmRequest1, customOAuth2User);
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            } finally {
                countDownLatch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                paymentService.confirmPayment(confirmRequest2, customOAuth2User);
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            } finally {
                countDownLatch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                paymentService.confirmPayment(confirmRequest3, customOAuth2User);
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            } finally {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();

        final MemberEntity updatedMemberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);

        assertEquals(18, updatedMemberEntity.getBatteryCount(), "Remaining batteries should be 18");
    }

    private void simulatePaymentSuccess(String paymentKey, BigDecimal amount, UUID orderId) {
        TossPaymentRequest paymentRequest = new TossPaymentRequest(paymentKey, orderId, amount);
        TossPaymentSuccessResponse successResponse = createMockTossSuccessResponse(paymentKey, amount, orderId);

        when(paymentGatewayClient.payment(eq(paymentRequest)))
                .thenReturn(ClientResponse.success(successResponse))
                .thenReturn(ClientResponse.failure());
    }

    private TossPaymentSuccessResponse createMockTossSuccessResponse(String paymentKey, BigDecimal amount,
                                                                     UUID orderId) {
        return new TossPaymentSuccessResponse(
                "1.0",
                paymentKey,
                "Normal",
                orderId.toString(),
                "Test Order",
                "testMid",
                "KRW",
                "Card",
                amount,
                amount,
                "Success",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                false,
                "transactionKey",
                amount,
                BigDecimal.ZERO,
                false,
                BigDecimal.ZERO,
                0,
                Collections.emptyList(),
                new TossPaymentSuccessResponse.Card(amount, "issuerCode", "acquirerCode", "number", 0, "approveNo",
                        false,
                        "cardType", "ownerType", "acquireStatus", false, "interestPayer"),
                new TossPaymentSuccessResponse.VirtualAccount("accountType", "accountNumber", "bankCode",
                        "customerName",
                        OffsetDateTime.now(), "refundStatus", false, "settlementStatus",
                        new TossPaymentSuccessResponse.RefundReceiveAccount("bankCode", "accountNumber", "holderName"),
                        "secret"),
                new TossPaymentSuccessResponse.MobilePhone("customerMobilePhone", "settlementStatus", "receiptUrl"),
                new TossPaymentSuccessResponse.GiftCertificate("approveNo", "settlementStatus"),
                new TossPaymentSuccessResponse.Transfer("bankCode", "settlementStatus"),
                new TossPaymentSuccessResponse.Receipt("url"),
                new TossPaymentSuccessResponse.Checkout("url"),
                new TossPaymentSuccessResponse.EasyPay("Toss", amount, BigDecimal.ZERO),
                new TossPaymentSuccessResponse.Failure("code", "message"),
                new TossPaymentSuccessResponse.CashReceipt("type", "receiptKey", "issueNumber", "receiptUrl", amount,
                        BigDecimal.ZERO, "orderId", "orderName", "transactionType", "businessNumber", "issueStatus",
                        "customerIdentityNumber", OffsetDateTime.now(),
                        new TossPaymentSuccessResponse.Failure("code", "message")),
                Collections.emptyList(),
                new TossPaymentSuccessResponse.Discount(0),
                "KR",
                "secret",
                false
        );
    }
}
