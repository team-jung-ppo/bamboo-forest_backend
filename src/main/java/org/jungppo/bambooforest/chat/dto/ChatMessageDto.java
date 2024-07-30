package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageDto {
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private String chatBotType;

    public enum MessageType {
        ENTER, TALK, LEAVE //입장, 채팅, 퇴장
    }

    public static ChatMessageDto create(MessageType type, String roomId, String sender, String message,
            String chatBotType) {
        return new ChatMessageDto(type, roomId, sender, message, chatBotType);
    }
}
