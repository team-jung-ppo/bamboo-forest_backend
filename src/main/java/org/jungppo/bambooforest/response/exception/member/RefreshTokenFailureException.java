package org.jungppo.bambooforest.response.exception.member;

import org.jungppo.bambooforest.response.exception.common.ExceptionType;

public class RefreshTokenFailureException extends MemberBusinessException {
    public RefreshTokenFailureException() {
        super(ExceptionType.REFRESH_TOKEN_FAILURE_EXCEPTION);
    }
}
