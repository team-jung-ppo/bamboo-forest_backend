package org.jungppo.bambooforest.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageDto {
    @NotNull(message = "Message type cannot be null")
    private MessageType type;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotBlank(message = "ChatBot name cannot be blank")
    private String chatBotName;

    public enum MessageType {
        ENTER, TALK, LEAVE //입장, 채팅, 퇴장
    }

    @JsonCreator
    public static ChatMessageDto from(
        @JsonProperty("type") MessageType type,
        @JsonProperty("message") String message,
        @JsonProperty("chatBotType") String chatBotType
    ) {
        return new ChatMessageDto(type, message, chatBotType);
    }
}
