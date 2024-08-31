package org.jungppo.bambooforest.chatbot.fixture;

import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;

public class ChatBotPurchaseRequestFixture {

    public static ChatBotPurchaseRequest createChatBotPurchaseRequest(final String chatBotItemName) {
        return new ChatBotPurchaseRequest(chatBotItemName);
    }
}
