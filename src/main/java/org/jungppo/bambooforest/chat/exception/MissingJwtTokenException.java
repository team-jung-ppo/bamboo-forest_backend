package org.jungppo.bambooforest.chat.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.AUTHENTICATION_ENTRY_POINT_EXCEPTION;

public class MissingJwtTokenException extends JwtBusinessException {
    public MissingJwtTokenException() {
        super(AUTHENTICATION_ENTRY_POINT_EXCEPTION);
    }
}
