package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatBotMessageDto {
    private String message;
    private String chatBotType;

    public static ChatBotMessageDto of(final ChatMessageDto chatMessageDto, String chatBotName) {
        return new ChatBotMessageDto(chatMessageDto.getMessage(), chatBotName);
    }
}
