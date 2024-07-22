package org.jungppo.bambooforest.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;

@Getter
@RequiredArgsConstructor
public class ChatBotPurchaseDto {
    private final Long id;
    private final int amount;
    private final ChatBotItemDto chatBotItem;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    public static ChatBotPurchaseDto from(final ChatBotPurchaseEntity chatBotPurchaseEntity) {
        return new ChatBotPurchaseDto(
                chatBotPurchaseEntity.getId(),
                chatBotPurchaseEntity.getAmount(),
                ChatBotItemDto.from(chatBotPurchaseEntity.getChatBotItem()),
                chatBotPurchaseEntity.getCreatedAt()
        );
    }
}
