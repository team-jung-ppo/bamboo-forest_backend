package org.jungppo.bambooforest.chat.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class SocketBusinessException extends BusinessException {

    public SocketBusinessException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public SocketBusinessException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
