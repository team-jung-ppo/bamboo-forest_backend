package org.jungppo.bambooforest.chat.domain.repository;

import java.util.Optional;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByRoomId(String roomId);
}
