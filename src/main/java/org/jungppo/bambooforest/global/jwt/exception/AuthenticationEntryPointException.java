package org.jungppo.bambooforest.global.jwt.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.AUTHENTICATION_ENTRY_POINT_EXCEPTION;

public class AuthenticationEntryPointException extends JwtBusinessException {
    public AuthenticationEntryPointException() {
        super(AUTHENTICATION_ENTRY_POINT_EXCEPTION);
    }
}
