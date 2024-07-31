package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatListDto {
    private String userMessage;
    private String botMessage;
    private String chatbotName;

    public static ChatListDto from(ChatMessageEntity lastMessage) {
        return new ChatListDto(lastMessage.getUserMessage(), lastMessage.getBotMessage(), lastMessage.getChatbotName());
    }
}
