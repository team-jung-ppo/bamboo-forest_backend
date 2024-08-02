package org.jungppo.bambooforest.chat.dto;

import com.fasterxml.jackson.databind.JsonNode;

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

    @NotBlank(message = "Room ID cannot be blank")
    private String roomId;

    @NotNull(message = "Member ID cannot be null")
    private Long memberId;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotBlank(message = "ChatBot type cannot be blank")
    private String chatBotType;

    public enum MessageType {
        ENTER, TALK, LEAVE //입장, 채팅, 퇴장
    }

    public static ChatMessageDto from(JsonNode jsonNode) {
        MessageType messageType = MessageType.valueOf(jsonNode.get("type").asText());
        String roomId = jsonNode.get("roomId").asText();
        Long memberId = jsonNode.get("memberId").asLong();
        String content = jsonNode.get("message").asText();
        String chatBotType = jsonNode.get("chatBotType").asText();

        return new ChatMessageDto(messageType, roomId, memberId, content, chatBotType);
    }
}
