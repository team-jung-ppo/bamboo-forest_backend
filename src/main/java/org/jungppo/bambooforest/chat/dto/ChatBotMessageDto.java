package org.jungppo.bambooforest.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatBotMessageDto {
    private String content;
    private String chatBotType;

    @Builder
    public ChatBotMessageDto(String content, String chatBotType) {
        this.content = content;
        this.chatBotType = chatBotType;
    }
}
