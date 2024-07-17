package org.jungppo.bambooforest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.service.ChatBotService;
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

@SpringBootTest
@ActiveProfiles("test")
public class ChatBotServiceConcurrencyTest {

    @Autowired
    private ChatBotService chatBotService;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 테스트에서 @Transactional과 ExecutorService가 생성한 스레드의 Transactional이 다름. ExecutorService에서 초기 데이터를 조회하지 못함.
     * 테스트에서@Transactional를 사용하지 않고 명시적으로 데이터베이스 초기화.
     */
    @Autowired
    private DatabaseCleaner databaseCleaner;

    private CustomOAuth2User customOAuth2User;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();

        MemberEntity memberEntity = MemberEntity.builder()
                .name("testUser")
                .oAuth2(OAuth2Type.OAUTH2_GITHUB)
                .username("testUser")
                .profileImage("testProfileImage")
                .role(RoleType.ROLE_USER)
                .build();

        memberEntity.addBatteries(100);
        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        customOAuth2User = new CustomOAuth2User(savedMemberEntity.getId(), savedMemberEntity.getRole(),
                savedMemberEntity.getOAuth2());
    }

    @Test
    void testSequentialPurchaseRequests() {
        ChatBotPurchaseRequest purchaseRequest1 = new ChatBotPurchaseRequest("아저씨 챗봇");
        ChatBotPurchaseRequest purchaseRequest2 = new ChatBotPurchaseRequest("아줌마 챗봇");
        ChatBotPurchaseRequest purchaseRequest3 = new ChatBotPurchaseRequest("어린이 챗봇");

        try {
            chatBotService.purchaseChatBot(purchaseRequest1, customOAuth2User);
            chatBotService.purchaseChatBot(purchaseRequest2, customOAuth2User);
            chatBotService.purchaseChatBot(purchaseRequest3, customOAuth2User);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        MemberEntity updatedMemberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(() -> new IllegalStateException("Member not found"));

        System.out.println("Remaining batteries: " + updatedMemberEntity.getBatteryCount());
        System.out.println("Purchased chatbots: " + updatedMemberEntity.getChatBots());
    }
    
    @Test
    void testConcurrentPurchaseRequests() throws InterruptedException {
        final int numberOfThreads = 3;
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        ChatBotPurchaseRequest purchaseRequest1 = new ChatBotPurchaseRequest("아저씨 챗봇");
        ChatBotPurchaseRequest purchaseRequest2 = new ChatBotPurchaseRequest("아줌마 챗봇");
        ChatBotPurchaseRequest purchaseRequest3 = new ChatBotPurchaseRequest("어린이 챗봇");

        ChatBotPurchaseRequest[] purchaseRequests = {purchaseRequest1, purchaseRequest2, purchaseRequest3};

        for (ChatBotPurchaseRequest purchaseRequest : purchaseRequests) {
            executorService.submit(() -> {
                try {
                    chatBotService.purchaseChatBot(purchaseRequest, customOAuth2User);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        MemberEntity updatedMemberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(() -> new IllegalStateException("Member not found"));

        System.out.println("Remaining batteries: " + updatedMemberEntity.getBatteryCount());
        System.out.println("Purchased chatbots: " + updatedMemberEntity.getChatBots());
    }
}
