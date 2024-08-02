package org.jungppo.bambooforest.chat.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageListDto {
    private Long id;
    private String userMessage;
    private String botMessage;
    private LocalDateTime createdAt;

    public static ChatMessageListDto from(ChatMessageEntity lastMessage) {
        return new ChatMessageListDto(lastMessage.getId(), lastMessage.getUserMessage(), lastMessage.getBotMessage(), lastMessage.getCreatedAt());
    }
}
