package org.jungppo.bambooforest.chat.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.ACCESS_DENIED_EXCEPTION;

public class JwtValidationException extends JwtBusinessException {
    public JwtValidationException() {
        super(ACCESS_DENIED_EXCEPTION);
    }
}
