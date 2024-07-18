package org.jungppo.bambooforest.chatbot.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class ChatBotBusinessException extends BusinessException {
    public ChatBotBusinessException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public ChatBotBusinessException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
