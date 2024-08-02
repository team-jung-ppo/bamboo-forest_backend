package org.jungppo.bambooforest.chat.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.SOCKET_MISSING_REQUIRED_ATTRIBUTES;

public class MissingRequiredAttributesException extends SocketBusinessException {
    public MissingRequiredAttributesException() {
        super(SOCKET_MISSING_REQUIRED_ATTRIBUTES);
    }
}
