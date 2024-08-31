package org.jungppo.bambooforest.chatbot.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.AUNT_CHATBOT;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.CHILD_CHATBOT;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.UNCLE_CHATBOT;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseEntityFixture.createChatBotPurchaseEntity;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseRequestFixture.createChatBotPurchaseRequest;
import static org.jungppo.bambooforest.global.oauth2.fixture.CustomOAuth2UserFixture.createCustomOAuth2User;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.createMemberEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.jungppo.bambooforest.battery.exception.BatteryInsufficientException;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.jungppo.bambooforest.chatbot.domain.repository.ChatBotPurchaseRepository;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.exception.ChatBotAlreadyOwnedException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotNotAvailableException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotNotFoundException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotPurchaseNotFoundException;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatBotPurchaseServiceTest {

    @InjectMocks
    private ChatBotPurchaseService chatBotPurchaseService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatBotPurchaseRepository chatBotPurchaseRepository;

    private CustomOAuth2User customOAuth2User;
    private MemberEntity memberEntity;

    @BeforeEach
    void setup() {
        customOAuth2User = createCustomOAuth2User(1L, null, null);
        memberEntity = createMemberEntity(customOAuth2User.getId(), customOAuth2User.getOAuth2Type(),
                "username", "profileUrl", customOAuth2User.getRoleType());
    }

    @Test
    void testPurchaseChatBot() {
        // given
        final ChatBotPurchaseRequest purchaseRequest =
                createChatBotPurchaseRequest(UNCLE_CHATBOT.getName());
        final ChatBotPurchaseEntity purchaseEntity =
                createChatBotPurchaseEntity(1L, 100, UNCLE_CHATBOT, memberEntity);

        when(memberRepository.findByIdWithOptimisticLock(eq(customOAuth2User.getId()))).thenReturn(
                Optional.of(memberEntity));
        when(chatBotPurchaseRepository.save(any(ChatBotPurchaseEntity.class))).thenReturn(purchaseEntity);

        // when
        final Long purchaseId = chatBotPurchaseService.purchaseChatBot(purchaseRequest, customOAuth2User);

        // then
        assertSoftly(softly -> {
            softly.assertThat(purchaseId).isEqualTo(purchaseEntity.getId());
            verify(memberRepository).findByIdWithOptimisticLock(eq(customOAuth2User.getId()));
            verify(chatBotPurchaseRepository).save(any(ChatBotPurchaseEntity.class));
        });
    }

    @Test
    void testPurchaseChatBot_PurchaseNotFound() {
        // given
        final ChatBotPurchaseRequest invalidPurchaseRequest = createChatBotPurchaseRequest("InvalidChatBot");

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(invalidPurchaseRequest, customOAuth2User))
                .isInstanceOf(ChatBotNotFoundException.class);
    }

    @Test
    void testPurchaseChatBot_ChatBotNotAvailable() {
        // given
        final ChatBotPurchaseRequest childPurchaseRequest = createChatBotPurchaseRequest(CHILD_CHATBOT.getName());

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(childPurchaseRequest, customOAuth2User))
                .isInstanceOf(ChatBotNotAvailableException.class);
    }

    @Test
    void testPurchaseChatBot_MemberNotFound() {
        // given
        final ChatBotPurchaseRequest unclePurchaseRequest = createChatBotPurchaseRequest(UNCLE_CHATBOT.getName());

        when(memberRepository.findByIdWithOptimisticLock(eq(customOAuth2User.getId()))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(unclePurchaseRequest, customOAuth2User))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void testPurchaseChatBot_ChatBotAlreadyOwned() {
        // given
        final ChatBotPurchaseRequest unclePurchaseRequest = createChatBotPurchaseRequest(UNCLE_CHATBOT.getName());

        memberEntity.addChatBot(UNCLE_CHATBOT);
        when(memberRepository.findByIdWithOptimisticLock(eq(customOAuth2User.getId()))).thenReturn(
                Optional.of(memberEntity));

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(unclePurchaseRequest, customOAuth2User))
                .isInstanceOf(ChatBotAlreadyOwnedException.class);
    }

    @Test
    void testPurchaseChatBot_BatteryInsufficient() {
        // given
        final ChatBotPurchaseRequest auntPurchaseRequest = createChatBotPurchaseRequest(AUNT_CHATBOT.getName());

        memberEntity.subtractBatteries(memberEntity.getBatteryCount());
        when(memberRepository.findByIdWithOptimisticLock(eq(customOAuth2User.getId()))).thenReturn(
                Optional.of(memberEntity));

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(auntPurchaseRequest, customOAuth2User))
                .isInstanceOf(BatteryInsufficientException.class);
    }

    @Test
    void testGetChatBotPurchase() {
        // given
        final ChatBotPurchaseEntity purchaseEntity = createChatBotPurchaseEntity(1L, 100, UNCLE_CHATBOT,
                createMemberEntity(customOAuth2User.getId(), customOAuth2User.getOAuth2Type(),
                        "username", "profileUrl", customOAuth2User.getRoleType()));

        when(chatBotPurchaseRepository.findById(eq(purchaseEntity.getId()))).thenReturn(Optional.of(purchaseEntity));

        // when
        final ChatBotPurchaseDto purchaseDto = chatBotPurchaseService.getChatBotPurchase(purchaseEntity.getId(),
                customOAuth2User);

        // then
        assertSoftly(softly -> {
            softly.assertThat(purchaseDto.getId()).isEqualTo(purchaseEntity.getId());
            verify(chatBotPurchaseRepository).findById(eq(purchaseEntity.getId()));
        });
    }

    @Test
    void testGetChatBotPurchase_PurchaseNotFound() {
        // given
        final Long purchaseId = 999L;

        when(chatBotPurchaseRepository.findById(eq(purchaseId))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.getChatBotPurchase(purchaseId, customOAuth2User))
                .isInstanceOf(ChatBotPurchaseNotFoundException.class);
    }

    @Test
    void testGetChatBotPurchases() {
        // given
        final ChatBotPurchaseEntity purchaseEntity = createChatBotPurchaseEntity(1L, 100, UNCLE_CHATBOT,
                createMemberEntity(customOAuth2User.getId(), customOAuth2User.getOAuth2Type(),
                        "username", "profileUrl", customOAuth2User.getRoleType()));

        when(chatBotPurchaseRepository.findAllByMemberIdOrderByCreatedAtDesc(eq(customOAuth2User.getId())))
                .thenReturn(List.of(purchaseEntity));

        // when
        final List<ChatBotPurchaseDto> purchases = chatBotPurchaseService.getChatBotPurchases(customOAuth2User);

        // then
        assertSoftly(softly -> {
            softly.assertThat(purchases).hasSize(1);
            softly.assertThat(purchases.get(0).getId()).isEqualTo(purchaseEntity.getId());
            verify(chatBotPurchaseRepository).findAllByMemberIdOrderByCreatedAtDesc(eq(customOAuth2User.getId()));
        });
    }
}
