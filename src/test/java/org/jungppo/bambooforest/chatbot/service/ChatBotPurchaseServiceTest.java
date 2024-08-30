package org.jungppo.bambooforest.chatbot.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.UNCLE_CHATBOT;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseEntityFixture.UNCLE_PURCHASE_ENTITY;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseRequestFixture.AUNT_PURCHASE_REQUEST;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseRequestFixture.CHILD_PURCHASE_REQUEST;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseRequestFixture.INVALID_PURCHASE_REQUEST;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseRequestFixture.UNCLE_PURCHASE_REQUEST;
import static org.jungppo.bambooforest.global.oauth2.fixture.CustomOAuth2UserFixture.CUSTOM_OAUTH2_USER;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.MEMBER_ENTITY;
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
import org.jungppo.bambooforest.chatbot.exception.ChatBotAlreadyOwnedException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotNotAvailableException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotNotFoundException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotPurchaseNotFoundException;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
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

    @Test
    void testPurchaseChatBot() {
        // given
        when(memberRepository.findByIdWithOptimisticLock(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(Optional.of(MEMBER_ENTITY));
        when(chatBotPurchaseRepository.save(any(ChatBotPurchaseEntity.class)))
                .thenReturn(UNCLE_PURCHASE_ENTITY);

        // when
        final Long purchaseId = chatBotPurchaseService.purchaseChatBot(UNCLE_PURCHASE_REQUEST, CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(purchaseId).isEqualTo(UNCLE_PURCHASE_ENTITY.getId());
            verify(memberRepository).findByIdWithOptimisticLock(eq(CUSTOM_OAUTH2_USER.getId()));
            verify(chatBotPurchaseRepository).save(any(ChatBotPurchaseEntity.class));
        });
    }

    @Test
    void testPurchaseChatBot_PurchaseNotFound() {
        // given & when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(INVALID_PURCHASE_REQUEST, CUSTOM_OAUTH2_USER))
                .isInstanceOf(ChatBotNotFoundException.class);
    }

    @Test
    void testPurchaseChatBot_ChatBotNotAvailable() {
        // given & when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(CHILD_PURCHASE_REQUEST,
                CUSTOM_OAUTH2_USER))
                .isInstanceOf(ChatBotNotAvailableException.class);
    }

    @Test
    void testPurchaseChatBot_MemberNotFound() {
        // given
        when(memberRepository.findByIdWithOptimisticLock(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(UNCLE_PURCHASE_REQUEST, CUSTOM_OAUTH2_USER))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void testPurchaseChatBot_ChatBotAlreadyOwned() {
        // given
        MEMBER_ENTITY.addChatBot(UNCLE_CHATBOT);
        when(memberRepository.findByIdWithOptimisticLock(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(Optional.of(MEMBER_ENTITY));

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(UNCLE_PURCHASE_REQUEST, CUSTOM_OAUTH2_USER))
                .isInstanceOf(ChatBotAlreadyOwnedException.class);
    }

    @Test
    void testPurchaseChatBot_BatteryInsufficient() {
        // given
        MEMBER_ENTITY.subtractBatteries(MEMBER_ENTITY.getBatteryCount());
        when(memberRepository.findByIdWithOptimisticLock(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(Optional.of(MEMBER_ENTITY));

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.purchaseChatBot(AUNT_PURCHASE_REQUEST, CUSTOM_OAUTH2_USER))
                .isInstanceOf(BatteryInsufficientException.class);
    }

    @Test
    void testGetChatBotPurchase() {
        // given
        final Long purchaseId = UNCLE_PURCHASE_ENTITY.getId();
        when(chatBotPurchaseRepository.findById(eq(purchaseId)))
                .thenReturn(Optional.of(UNCLE_PURCHASE_ENTITY));

        // when
        final ChatBotPurchaseDto purchaseDto = chatBotPurchaseService.getChatBotPurchase(purchaseId,
                CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(purchaseDto.getId()).isEqualTo(UNCLE_PURCHASE_ENTITY.getId());
            verify(chatBotPurchaseRepository).findById(eq(purchaseId));
        });
    }

    @Test
    void testGetChatBotPurchase_PurchaseNotFound() {
        // given
        final Long purchaseId = 999L;  // non-existing ID
        when(chatBotPurchaseRepository.findById(eq(purchaseId)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatBotPurchaseService.getChatBotPurchase(purchaseId, CUSTOM_OAUTH2_USER))
                .isInstanceOf(ChatBotPurchaseNotFoundException.class);
    }

    @Test
    void testGetChatBotPurchases() {
        // given
        when(chatBotPurchaseRepository.findAllByMemberIdOrderByCreatedAtDesc(eq(CUSTOM_OAUTH2_USER.getId())))
                .thenReturn(List.of(UNCLE_PURCHASE_ENTITY));

        // when
        final List<ChatBotPurchaseDto> purchases = chatBotPurchaseService.getChatBotPurchases(CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(purchases).hasSize(1);
            softly.assertThat(purchases.get(0).getId()).isEqualTo(UNCLE_PURCHASE_ENTITY.getId());
            verify(chatBotPurchaseRepository).findAllByMemberIdOrderByCreatedAtDesc(eq(CUSTOM_OAUTH2_USER.getId()));
        });
    }
}
