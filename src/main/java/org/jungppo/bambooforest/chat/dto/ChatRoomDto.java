package org.jungppo.bambooforest.chat.dto;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDto {
    private String roomId;
    private String name;

    public static ChatRoomDto create(ChatRoomEntity chatRoomEntity) {
        return new ChatRoomDto(chatRoomEntity.getRoomId(), chatRoomEntity.getName());
    }
}
