package org.jungppo.bambooforest.chat.dto;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDto {
    private String roomId;
    
    public static ChatRoomDto from(final ChatRoomEntity chatRoomEntity) {
        return new ChatRoomDto(chatRoomEntity.getRoomId());
    }
}
