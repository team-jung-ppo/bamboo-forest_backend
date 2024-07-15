package org.jungppo.bambooforest.member.exception;

import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public class RefreshTokenFailureException extends MemberBusinessException {
    public RefreshTokenFailureException() {
        super(ExceptionType.REFRESH_TOKEN_FAILURE_EXCEPTION);
    }
}
