package org.jungppo.bambooforest.performance;

import java.util.List;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.dto.PaymentDto;
import org.jungppo.bambooforest.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServicePerformanceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testGetPaymentsPerformance() {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(1L, RoleType.ROLE_USER, OAuth2Type.OAUTH2_GITHUB);

        long startTime = System.nanoTime();
        List<PaymentDto> payments = paymentService.getPayments(customOAuth2User);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration + " nanoseconds");
        System.out.println("payments: " + payments);
    }
}
