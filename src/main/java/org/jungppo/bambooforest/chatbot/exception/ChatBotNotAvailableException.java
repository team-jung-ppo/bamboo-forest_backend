package org.jungppo.bambooforest.chatbot.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.CHATBOT_NOT_AVAILABLE_EXCEPTION;

public class ChatBotNotAvailableException extends ChatBotBusinessException {
    public ChatBotNotAvailableException() {
        super(CHATBOT_NOT_AVAILABLE_EXCEPTION);
    }
}
