package org.jungppo.bambooforest.chatbot.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.CHATBOT_TYPE_MISMATCH_EXCEPTION;

public class ChatBotTypeMismatchException extends ChatBotBusinessException {
    public ChatBotTypeMismatchException() {
        super(CHATBOT_TYPE_MISMATCH_EXCEPTION);
    }
}
