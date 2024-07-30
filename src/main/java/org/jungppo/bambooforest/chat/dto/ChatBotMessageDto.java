package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatBotMessageDto {
    private String message;
    private String chatBotType;

    public static ChatBotMessageDto from(ChatMessageDto chatMessageDto) {
        return new ChatBotMessageDto(chatMessageDto.getMessage(), chatMessageDto.getChatBotType());
    }
}
