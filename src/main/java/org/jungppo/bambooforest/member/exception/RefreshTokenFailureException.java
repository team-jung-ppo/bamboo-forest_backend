package org.jungppo.bambooforest.member.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.REFRESH_TOKEN_FAILURE_EXCEPTION;

public class RefreshTokenFailureException extends MemberBusinessException {
    public RefreshTokenFailureException() {
        super(REFRESH_TOKEN_FAILURE_EXCEPTION);
    }
}
