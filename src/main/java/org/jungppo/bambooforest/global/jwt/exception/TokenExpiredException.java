package org.jungppo.bambooforest.global.jwt.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.JWT_EXPIRED_EXCEPTION;

public class TokenExpiredException extends JwtBusinessException {
    public TokenExpiredException() {
        super(JWT_EXPIRED_EXCEPTION);
    }
}
