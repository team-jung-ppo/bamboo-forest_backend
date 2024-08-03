package org.jungppo.bambooforest.global.jwt.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class JwtBusinessException extends BusinessException {
    public JwtBusinessException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public JwtBusinessException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
