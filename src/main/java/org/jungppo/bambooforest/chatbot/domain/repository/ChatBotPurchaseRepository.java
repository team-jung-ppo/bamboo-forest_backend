package org.jungppo.bambooforest.chatbot.domain.repository;

import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatBotPurchaseRepository extends JpaRepository<ChatBotPurchaseEntity, Long>,
        QuerydslChatBotPurchaseRepository {

}
