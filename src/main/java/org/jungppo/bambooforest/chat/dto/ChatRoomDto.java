package org.jungppo.bambooforest.chat.dto;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDto {
    private String roomId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    public static ChatRoomDto from(final ChatRoomEntity chatRoomEntity) {
        return new ChatRoomDto(chatRoomEntity.getRoomId(), chatRoomEntity.getCreatedAt());
    }
}
