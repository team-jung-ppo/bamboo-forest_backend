package org.jungppo.bambooforest.chat.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public class RoomNotFoundException extends BusinessException {

    public RoomNotFoundException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public RoomNotFoundException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
