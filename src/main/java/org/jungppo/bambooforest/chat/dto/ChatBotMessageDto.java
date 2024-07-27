package org.jungppo.bambooforest.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatBotMessageDto {
    private String message;
    private String chatBotType;

    @Builder
    public ChatBotMessageDto(String message, String chatBotType) {
        this.message = message;
        this.chatBotType = chatBotType;
    }
}
