package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDto {
    private String roomId;
    private String name;

    public static ChatRoomDto create(String roomId, String name) {
        return new ChatRoomDto(roomId, name);
    }
}
