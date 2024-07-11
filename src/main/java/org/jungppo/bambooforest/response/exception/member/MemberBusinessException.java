package org.jungppo.bambooforest.response.exception.member;

import org.jungppo.bambooforest.response.exception.common.BusinessException;
import org.jungppo.bambooforest.response.exception.common.ExceptionType;

public abstract class MemberBusinessException extends BusinessException {

    public MemberBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public MemberBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
