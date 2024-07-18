package org.jungppo.bambooforest.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatMessageDto {
    private MessageType type;
    private String roomId;
    private String sender;
    private String content;
    private String chatBotType;

    public enum MessageType {
        ENTER, TALK, LEAVE //입장, 채팅, 퇴장
    }

    @Builder
    public ChatMessageDto(MessageType type, String roomId, String sender, String content, String chatBotType) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
        this.chatBotType = chatBotType;
    }
}
