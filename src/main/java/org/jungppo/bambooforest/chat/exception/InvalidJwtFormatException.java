package org.jungppo.bambooforest.chat.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.INVALID_JWT_FORMAT_EXCEPTION;

public class InvalidJwtFormatException extends JwtBusinessException {
    public InvalidJwtFormatException() {
        super(INVALID_JWT_FORMAT_EXCEPTION);
    }
}
