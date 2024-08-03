package org.jungppo.bambooforest.chat.domain.repository;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuerydslChatRoomRepository {
    Page<ChatRoomEntity> findChatRoomsByMemberId(Long memberId, Pageable pageable);
}
