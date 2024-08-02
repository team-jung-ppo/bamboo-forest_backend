package org.jungppo.bambooforest.chat.domain.repository;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuerydslChatMessageRepository {
    Page<ChatMessageEntity> findPagedMessagesByMemberId(Long roomId, Long memberId, Pageable pageable);
}
