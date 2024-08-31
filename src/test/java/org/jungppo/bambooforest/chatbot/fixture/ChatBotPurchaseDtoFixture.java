package org.jungppo.bambooforest.chatbot.fixture;

import java.time.LocalDateTime;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;

public class ChatBotPurchaseDtoFixture {

    public static ChatBotPurchaseDto createChatBotPurchaseDto(final Long id, final int amount,
                                                              final ChatBotItemDto chatBotItemDto) {
        return new ChatBotPurchaseDto(id, amount, chatBotItemDto, LocalDateTime.now());
    }
}
