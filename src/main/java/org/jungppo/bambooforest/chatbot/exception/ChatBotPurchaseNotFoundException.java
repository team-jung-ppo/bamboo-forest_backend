package org.jungppo.bambooforest.chatbot.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.CHATBOT_PURCHASE_NOT_FOUND_EXCEPTION;

public class ChatBotPurchaseNotFoundException extends ChatBotBusinessException {
    public ChatBotPurchaseNotFoundException() {
        super(CHATBOT_PURCHASE_NOT_FOUND_EXCEPTION);
    }
}
