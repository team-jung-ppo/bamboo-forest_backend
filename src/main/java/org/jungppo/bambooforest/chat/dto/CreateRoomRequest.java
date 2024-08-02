package org.jungppo.bambooforest.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRoomRequest {
    @NotBlank(message = "ChatBot type cannot be blank")
    private String chatBotType;
}
