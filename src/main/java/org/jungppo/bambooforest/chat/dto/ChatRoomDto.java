package org.jungppo.bambooforest.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomDto {
    private String roomId;
    private String name;

    @Builder
    public ChatRoomDto(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }
}
