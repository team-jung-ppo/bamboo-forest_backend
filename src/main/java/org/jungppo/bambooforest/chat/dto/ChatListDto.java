package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatListDto {
    private String roomId;
    private String roomName;
    private String userMessage;
    private String botMessage;
    private String chatbotName;

    public static ChatListDto of(ChatMessageEntity lastMessage) {
        return new ChatListDto(lastMessage.getChatRoom().getRoomId(), lastMessage.getChatRoom().getName(), lastMessage.getUserMessage(), lastMessage.getBotMessage(), lastMessage.getChatbotName());
    }
}
