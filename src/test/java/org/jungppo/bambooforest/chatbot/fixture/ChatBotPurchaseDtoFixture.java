package org.jungppo.bambooforest.chatbot.fixture;

import java.time.LocalDateTime;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;

public class ChatBotPurchaseDtoFixture {

    public static final ChatBotPurchaseDto UNCLE_PURCHASE_DTO = new ChatBotPurchaseDto(
            1L,
            0,
            ChatBotItemDtoFixture.UNCLE_CHATBOT_DTO,
            LocalDateTime.now()
    );

    public static final ChatBotPurchaseDto AUNT_PURCHASE_DTO = new ChatBotPurchaseDto(
            2L,
            3,
            ChatBotItemDtoFixture.AUNT_CHATBOT_DTO,
            LocalDateTime.now()
    );

    public static final ChatBotPurchaseDto CHILD_PURCHASE_DTO = new ChatBotPurchaseDto(
            3L,
            5,
            ChatBotItemDtoFixture.CHILD_CHATBOT_DTO,
            LocalDateTime.now()
    );
}
