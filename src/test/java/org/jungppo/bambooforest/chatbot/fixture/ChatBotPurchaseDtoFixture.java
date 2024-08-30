package org.jungppo.bambooforest.chatbot.fixture;

import java.time.LocalDateTime;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;

public class ChatBotPurchaseDtoFixture {

    private static final LocalDateTime NOW = LocalDateTime.now();

    public static final ChatBotPurchaseDto PURCHASE_DTO_UNCLE = new ChatBotPurchaseDto(
            1L,
            0,
            ChatBotItemDtoFixture.UNCLE_CHATBOT_DTO,
            NOW
    );

    public static final ChatBotPurchaseDto PURCHASE_DTO_AUNT = new ChatBotPurchaseDto(
            2L,
            3,
            ChatBotItemDtoFixture.AUNT_CHATBOT_DTO,
            NOW
    );

    public static final ChatBotPurchaseDto PURCHASE_DTO_CHILD = new ChatBotPurchaseDto(
            3L,
            5,
            ChatBotItemDtoFixture.CHILD_CHATBOT_DTO,
            NOW
    );
}
