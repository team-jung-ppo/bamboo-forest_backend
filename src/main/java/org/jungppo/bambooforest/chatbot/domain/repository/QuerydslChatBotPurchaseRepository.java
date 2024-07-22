package org.jungppo.bambooforest.chatbot.domain.repository;

import java.util.List;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;

public interface QuerydslChatBotPurchaseRepository {
    List<ChatBotPurchaseEntity> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
}
