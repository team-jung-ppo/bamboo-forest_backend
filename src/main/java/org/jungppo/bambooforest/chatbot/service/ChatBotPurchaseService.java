package org.jungppo.bambooforest.chatbot.service;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.jungppo.bambooforest.chatbot.domain.repository.ChatBotPurchaseRepository;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatBotPurchaseService {

    private final ChatBotPurchaseRepository chatBotPurchaseRepository;

    public void savePurchase(final ChatBotItem chatBotItem, final MemberEntity memberEntity) {
        final ChatBotPurchaseEntity purchaseEntity = ChatBotPurchaseEntity.builder()
                .chatBotItem(chatBotItem)
                .member(memberEntity)
                .build();

        chatBotPurchaseRepository.save(purchaseEntity);
    }
}
