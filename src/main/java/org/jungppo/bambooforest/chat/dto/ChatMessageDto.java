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

    public enum MessageType {
        ENTER, TALK, LEAVE //입장, 채팅, 퇴장
    }

    @JsonCreator
    public static ChatMessageDto of(
        @JsonProperty("type") MessageType type,
        @JsonProperty("message") String message
    ) {
        return new ChatMessageDto(type, message);
    }
}
