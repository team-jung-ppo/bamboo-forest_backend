package org.jungppo.bambooforest.member.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class MemberBusinessException extends BusinessException {

    public MemberBusinessException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public MemberBusinessException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
