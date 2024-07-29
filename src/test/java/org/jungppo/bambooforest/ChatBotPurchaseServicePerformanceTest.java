package org.jungppo.bambooforest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.jungppo.bambooforest.chatbot.domain.repository.ChatBotPurchaseRepository;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;
import org.jungppo.bambooforest.chatbot.service.ChatBotPurchaseService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ChatBotPurchaseServicePerformanceTest {

    @Autowired
    private ChatBotPurchaseService chatBotPurchaseService;

    @Autowired
    private ChatBotPurchaseRepository chatBotPurchaseRepository;

    @Autowired
    private MemberRepository memberRepository;

    private CustomOAuth2User customOAuth2User1;

    @BeforeEach
    void setUp() {
        MemberEntity memberEntity1 = createMember("testUser1", "testProfileImage1", OAuth2Type.OAUTH2_GITHUB);
        MemberEntity memberEntity2 = createMember("testUser2", "testProfileImage2", OAuth2Type.OAUTH2_KAKAO);
        MemberEntity memberEntity3 = createMember("testUser3", "testProfileImage3", OAuth2Type.OAUTH2_KAKAO);
        prepareChatBotPurchaseData(memberEntity1, memberEntity2, memberEntity3);

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

    private void prepareChatBotPurchaseData(MemberEntity memberEntity1, MemberEntity memberEntity2,
                                            MemberEntity memberEntity3) {
        for (int i = 0; i < 100; i++) {
            chatBotPurchaseRepository.save(createChatBotPurchaseEntity(memberEntity1, ChatBotItem.UNCLE_CHATBOT));
            chatBotPurchaseRepository.save(createChatBotPurchaseEntity(memberEntity2, ChatBotItem.AUNT_CHATBOT));
            chatBotPurchaseRepository.save(createChatBotPurchaseEntity(memberEntity3, ChatBotItem.CHILD_CHATBOT));
        }
    }

    private ChatBotPurchaseEntity createChatBotPurchaseEntity(MemberEntity memberEntity, ChatBotItem chatBotItem) {
        return ChatBotPurchaseEntity.builder()
                .amount(chatBotItem.getPrice())
                .chatBotItem(chatBotItem)
                .member(memberEntity)
                .build();
    }

    @Test
    void testGetChatBotPurchasesPerformance() {
        long startTime = System.nanoTime();
        List<ChatBotPurchaseDto> purchases = chatBotPurchaseService.getChatBotPurchases(customOAuth2User1);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Execution time: " + duration + " nanoseconds");
        assertNotNull(purchases);
        assertFalse(purchases.isEmpty(), "Purchases should not be empty");
    }
}
