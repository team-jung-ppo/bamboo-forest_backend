package org.jungppo.bambooforest.chat.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageDto {
    private MessageType type;
    private String roomId;
    private Long memberId;
    private String message;
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
