package org.jungppo.bambooforest.chat.dto;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDto {
    @NotBlank(message = "Room ID cannot be blank")
    private String roomId;
    
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String name;

    public static ChatRoomDto from(ChatRoomEntity chatRoomEntity) {
        return new ChatRoomDto(chatRoomEntity.getRoomId(), chatRoomEntity.getName());
    }
}
