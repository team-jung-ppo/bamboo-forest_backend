package org.jungppo.bambooforest.chatbot.fixture;

import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.AUNT_CHATBOT;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.CHILD_CHATBOT;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.UNCLE_CHATBOT;

import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;

public class ChatBotPurchaseRequestFixture {

    public static final ChatBotPurchaseRequest UNCLE_PURCHASE_REQUEST =
            new ChatBotPurchaseRequest(UNCLE_CHATBOT.getName());

    public static final ChatBotPurchaseRequest AUNT_PURCHASE_REQUEST =
            new ChatBotPurchaseRequest(AUNT_CHATBOT.getName());

    public static final ChatBotPurchaseRequest CHILD_PURCHASE_REQUEST =
            new ChatBotPurchaseRequest(CHILD_CHATBOT.getName());

    public static final ChatBotPurchaseRequest INVALID_PURCHASE_REQUEST =
            new ChatBotPurchaseRequest("invalidChatBotItemName");
}
