package org.jungppo.bambooforest.chatbot.domain.repository;

import java.util.Optional;

import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatBotPurchaseRepository extends JpaRepository<ChatBotPurchaseEntity, Long>,
        QuerydslChatBotPurchaseRepository {
        Optional<ChatBotPurchaseEntity> findByMemberId(Long memberId);
}
