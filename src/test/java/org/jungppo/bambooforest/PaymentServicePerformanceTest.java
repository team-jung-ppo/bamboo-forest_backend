package org.jungppo.bambooforest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.PaymentDto;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.jungppo.bambooforest.payment.domain.entity.PaymentStatusType;
import org.jungppo.bambooforest.payment.domain.repository.PaymentRepository;
import org.jungppo.bambooforest.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class PaymentServicePerformanceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    private CustomOAuth2User customOAuth2User1;

    @BeforeEach
    void setUp() {
        MemberEntity memberEntity1 = createMember("testUser1", "testProfileImage1", OAuth2Type.OAUTH2_GITHUB);
        MemberEntity memberEntity2 = createMember("testUser2", "testProfileImage2", OAuth2Type.OAUTH2_KAKAO);
        MemberEntity memberEntity3 = createMember("testUser3", "testProfileImage3", OAuth2Type.OAUTH2_KAKAO);
        preparePaymentData(memberEntity1, memberEntity2, memberEntity3);

        customOAuth2User1 = new CustomOAuth2User(memberEntity1.getId(), memberEntity1.getRole(),
                memberEntity1.getOAuth2());
    }

    private MemberEntity createMember(String username, String profileImage, OAuth2Type oAuth2Type) {
        MemberEntity memberEntity = MemberEntity.builder()
                .name(username)
                .oAuth2(oAuth2Type)
                .username(username)
                .profileImage(profileImage)
                .role(RoleType.ROLE_USER)
                .build();
        return memberRepository.save(memberEntity);
    }

    private void preparePaymentData(MemberEntity memberEntity1, MemberEntity memberEntity2,
                                    MemberEntity memberEntity3) {
        for (int i = 0; i < 10; i++) {
            paymentRepository.save(
                    createPaymentEntity(memberEntity1, "testKey1_" + i, 10 + i, BatteryItem.SMALL_BATTERY));
            paymentRepository.save(
                    createPaymentEntity(memberEntity2, "testKey2_" + i, 20 + i, BatteryItem.MEDIUM_BATTERY));
            paymentRepository.save(
                    createPaymentEntity(memberEntity3, "testKey3_" + i, 30 + i, BatteryItem.LARGE_BATTERY));
        }
    }

    private PaymentEntity createPaymentEntity(MemberEntity memberEntity, String key, int amount,
                                              BatteryItem batteryItem) {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .status(PaymentStatusType.COMPLETED)
                .batteryItem(batteryItem)
                .member(memberEntity)
                .build();
        paymentEntity.updatePaymentDetails(key, "TestProvider", BigDecimal.valueOf(amount));
        return paymentEntity;
    }

    @Test
    void testGetPaymentsPerformance() {
        long startTime = System.nanoTime();
        List<PaymentDto> payments = paymentService.getPayments(customOAuth2User1);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration + " nanoseconds");
        assertNotNull(payments);
        assertFalse(payments.isEmpty(), "Payments should not be empty");
    }
}
