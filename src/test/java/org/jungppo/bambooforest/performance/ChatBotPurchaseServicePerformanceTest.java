package org.jungppo.bambooforest.performance;

import java.util.List;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;
import org.jungppo.bambooforest.chatbot.service.ChatBotPurchaseService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ChatBotPurchaseServicePerformanceTest {

    @Autowired
    private ChatBotPurchaseService chatBotPurchaseService;

    @Test
    void testGetChatBotPurchasesPerformance() {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(1L, RoleType.ROLE_USER, OAuth2Type.OAUTH2_GITHUB);

        long startTime = System.nanoTime();
        List<ChatBotPurchaseDto> purchases = chatBotPurchaseService.getChatBotPurchases(customOAuth2User);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration + " nanoseconds");
        System.out.println("purchases: " + purchases);
    }
}
