package org.jungppo.bambooforest.chatbot.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.CHATBOT_NOT_FOUND_EXCEPTION;

public class ChatBotNotFoundException extends ChatBotBusinessException {
    public ChatBotNotFoundException() {
        super(CHATBOT_NOT_FOUND_EXCEPTION);
    }
}
