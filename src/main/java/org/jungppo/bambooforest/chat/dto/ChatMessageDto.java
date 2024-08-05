package org.jungppo.bambooforest.chat.dto;

import org.jungppo.bambooforest.chat.exception.ChatMessageValidationException;

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
    private ChatMessageType type;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private String token;   // 인증 토큰

    private String roomId;  // 룸 ID

    private Long memberId;  // 멤버 ID

    public enum ChatMessageType {
        AUTH, ENTER, TALK, LEAVE //인증, 입장, 채팅, 퇴장
    }

    @JsonCreator
    public static ChatMessageDto of(
        @JsonProperty("type") ChatMessageType type,
        @JsonProperty("message") String message,
        @JsonProperty("token") String token,
        @JsonProperty("roomId") String roomId,
        @JsonProperty("memberId") Long memberId
    ) {
        ChatMessageDto dto = new ChatMessageDto(type, message, token, roomId, memberId);
        dto.validate();
        return dto;
    }

    private void validate() {
        if (this.type == ChatMessageType.AUTH) {
            validateField(this.token);
            validateField(this.roomId);
            validateField(this.memberId);
        }
    }

    private void validateField(String field) {
        if (field == null || field.isBlank()) throw new ChatMessageValidationException();
    }

    private void validateField(Long field) {
        if (field == null) throw new ChatMessageValidationException();
    }
}
