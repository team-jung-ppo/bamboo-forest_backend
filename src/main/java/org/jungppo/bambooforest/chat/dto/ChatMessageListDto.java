package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageListDto {
    private String userMessage;
    private String botMessage;
    private String chatbotName;

    public static ChatMessageListDto from(ChatMessageEntity lastMessage) {
        return new ChatMessageListDto(lastMessage.getUserMessage(), lastMessage.getBotMessage(), lastMessage.getChatbotName());
    }
}
