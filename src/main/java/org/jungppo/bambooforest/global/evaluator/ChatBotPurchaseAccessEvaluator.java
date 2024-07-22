package org.jungppo.bambooforest.global.evaluator;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.jungppo.bambooforest.chatbot.domain.repository.ChatBotPurchaseRepository;
import org.jungppo.bambooforest.chatbot.exception.ChatBotPurchaseNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatBotPurchaseAccessEvaluator implements Evaluator<Long> {

    private final ChatBotPurchaseRepository chatBotPurchaseRepository;

    @Override
    public boolean isEligible(final Long targetId, final Long memberId) {
        final ChatBotPurchaseEntity chatBotPurchaseEntity = chatBotPurchaseRepository.findById(targetId)
                .orElseThrow(ChatBotPurchaseNotFoundException::new);
        return chatBotPurchaseEntity.getMember().getId().equals(memberId);
    }
}
