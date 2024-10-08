package org.jungppo.bambooforest.chatbot.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.jungppo.bambooforest.chatbot.domain.repository.ChatBotPurchaseRepository;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.exception.ChatBotNotFoundException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotPurchaseNotFoundException;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.repository.query.Param;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatBotPurchaseService {

    private final MemberRepository memberRepository;
    private final ChatBotPurchaseRepository chatBotPurchaseRepository;

    @Retryable(retryFor = {OptimisticLockingFailureException.class})
    @Transactional
    public Long purchaseChatBot(final ChatBotPurchaseRequest chatBotPurchaseRequest,
                                final CustomOAuth2User customOAuth2User) {
        final ChatBotItem chatBotItem = ChatBotItem.findByName(chatBotPurchaseRequest.getChatBotItemName())
                .orElseThrow(ChatBotNotFoundException::new);
        chatBotItem.validateAvailability();

        final MemberEntity memberEntity = memberRepository.findByIdWithOptimisticLock(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);
        memberEntity.subtractBatteries(chatBotItem.getPrice());
        memberEntity.addChatBot(chatBotItem);

        return savePurchase(chatBotItem, memberEntity).getId();
    }

    public ChatBotPurchaseEntity savePurchase(final ChatBotItem chatBotItem, final MemberEntity memberEntity) {
        final ChatBotPurchaseEntity purchaseEntity =
                ChatBotPurchaseEntity.of(chatBotItem.getPrice(), chatBotItem, memberEntity);
        return chatBotPurchaseRepository.save(purchaseEntity);
    }

    @PreAuthorize(value = "@chatBotPurchaseAccessEvaluator.isEligible(#chatBotPurchaseId, #customOAuth2User.getId())")
    public ChatBotPurchaseDto getChatBotPurchase(@Param(value = "chatBotPurchaseId") final Long chatBotPurchaseId,
                                                 @Param(value = "customOAuth2User") final CustomOAuth2User customOAuth2User) {
        final ChatBotPurchaseEntity chatBotPurchaseEntity = chatBotPurchaseRepository.findById(chatBotPurchaseId)
                .orElseThrow(ChatBotPurchaseNotFoundException::new);

        return ChatBotPurchaseDto.from(chatBotPurchaseEntity);
    }

    public List<ChatBotPurchaseDto> getChatBotPurchases(final CustomOAuth2User customOAuth2User) {
        final List<ChatBotPurchaseEntity> purchaseEntities = chatBotPurchaseRepository.findAllByMemberIdOrderByCreatedAtDesc(
                customOAuth2User.getId());

        return purchaseEntities.stream()
                .map(ChatBotPurchaseDto::from)
                .collect(Collectors.toList());
    }
}
