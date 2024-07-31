package org.jungppo.bambooforest.chat.domain.repository;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuerydslChatMessageRepository {
    List<ChatMessageEntity> findLastMessagesByMemberId(String roomId, Long memberId, Pageable pageable);
}
