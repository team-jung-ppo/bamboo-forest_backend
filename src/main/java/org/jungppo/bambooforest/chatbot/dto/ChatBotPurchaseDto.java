package org.jungppo.bambooforest.chatbot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;

@Getter
@RequiredArgsConstructor
public class ChatBotPurchaseDto {
    private final Long id;
    private final ChatBotItemDto chatBotItem;

    public static ChatBotPurchaseDto from(final ChatBotPurchaseEntity chatBotPurchaseEntity) {
        return new ChatBotPurchaseDto(
                chatBotPurchaseEntity.getId(),
                ChatBotItemDto.from(chatBotPurchaseEntity.getChatBotItem())
        );
    }
}
