package org.jungppo.bambooforest.chat.domain.repository;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long>, QuerydslChatMessageRepository {
    
}
