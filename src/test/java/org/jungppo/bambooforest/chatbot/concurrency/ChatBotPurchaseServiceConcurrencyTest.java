package org.jungppo.bambooforest.chatbot.concurrency;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.AUNT_CHATBOT;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.UNCLE_CHATBOT;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.service.ChatBotPurchaseService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.util.DatabaseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@SpringBootTest
@ActiveProfiles("test")
public class ChatBotPurchaseServiceConcurrencyTest {

    @Autowired
    private ChatBotPurchaseService chatBotPurchaseService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DatabaseUtils databaseUtils;

    @MockBean
    private ServletServerContainerFactoryBean servletServerContainerFactoryBean;

    private CustomOAuth2User customOAuth2User;

    @BeforeEach
    void setUp() {
        databaseUtils.clean();

        final MemberEntity memberEntity = MemberEntity.of("testUser", OAuth2Type.OAUTH2_GITHUB, "testUser",
                "testProfileImage", RoleType.ROLE_USER);
        memberEntity.addBatteries(100);

        final MemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        customOAuth2User = new CustomOAuth2User(savedMemberEntity.getId(), savedMemberEntity.getRole(),
                savedMemberEntity.getOAuth2());
    }

    @Test
    void testSequentialPurchaseRequests() {
        final ChatBotPurchaseRequest purchaseRequest1 = new ChatBotPurchaseRequest(UNCLE_CHATBOT.getName());
        final ChatBotPurchaseRequest purchaseRequest2 = new ChatBotPurchaseRequest(AUNT_CHATBOT.getName());

        try {
            chatBotPurchaseService.purchaseChatBot(purchaseRequest1, customOAuth2User);
            chatBotPurchaseService.purchaseChatBot(purchaseRequest2, customOAuth2User);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }

        final MemberEntity updatedMemberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);

        assertSoftly(softly -> {
            softly.assertThat(updatedMemberEntity.getBatteryCount())
                    .as("Remaining batteries should be 97")
                    .isEqualTo(97);
            softly.assertThat(updatedMemberEntity.getChatBots())
                    .as("Purchased chatbots should contain UNCLE_CHATBOT")
                    .contains(UNCLE_CHATBOT);
            softly.assertThat(updatedMemberEntity.getChatBots())
                    .as("Purchased chatbots should contain AUNT_CHATBOT")
                    .contains(AUNT_CHATBOT);
        });
    }

    @Test
    void testConcurrentPurchaseRequests() throws InterruptedException {
        final int numberOfThreads = 2;
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        final ChatBotPurchaseRequest purchaseRequest1 = new ChatBotPurchaseRequest(UNCLE_CHATBOT.getName());
        final ChatBotPurchaseRequest purchaseRequest2 = new ChatBotPurchaseRequest(AUNT_CHATBOT.getName());

        final ChatBotPurchaseRequest[] purchaseRequests = {purchaseRequest1, purchaseRequest2};

        for (final ChatBotPurchaseRequest purchaseRequest : purchaseRequests) {
            executorService.submit(() -> {
                try {
                    chatBotPurchaseService.purchaseChatBot(purchaseRequest, customOAuth2User);
                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        final MemberEntity updatedMemberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);

        assertSoftly(softly -> {
            softly.assertThat(updatedMemberEntity.getBatteryCount())
                    .as("Remaining batteries should be 97")
                    .isEqualTo(97);
            softly.assertThat(updatedMemberEntity.getChatBots())
                    .as("Purchased chatbots should contain UNCLE_CHATBOT")
                    .contains(UNCLE_CHATBOT);
            softly.assertThat(updatedMemberEntity.getChatBots())
                    .as("Purchased chatbots should contain AUNT_CHATBOT")
                    .contains(AUNT_CHATBOT);
        });
    }
}
