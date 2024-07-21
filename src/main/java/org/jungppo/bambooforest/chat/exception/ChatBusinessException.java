package org.jungppo.bambooforest.chat.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class ChatBusinessException extends BusinessException {

    public ChatBusinessException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public ChatBusinessException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
