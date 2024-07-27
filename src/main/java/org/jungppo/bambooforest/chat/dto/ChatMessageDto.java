package org.jungppo.bambooforest.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageDto {
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private String chatBotType;

    public enum MessageType {
        ENTER, TALK, LEAVE //입장, 채팅, 퇴장
    }

    @Builder
    public ChatMessageDto(MessageType type, String roomId, String sender, String message, String chatBotType) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.chatBotType = chatBotType;
    }
}
