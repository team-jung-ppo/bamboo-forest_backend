package org.jungppo.bambooforest.member.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class MemberBusinessException extends BusinessException {

    public MemberBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public MemberBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
