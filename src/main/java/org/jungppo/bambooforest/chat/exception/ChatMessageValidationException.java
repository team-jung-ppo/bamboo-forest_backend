package org.jungppo.bambooforest.chat.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.BIND_EXCEPTION;

public class ChatMessageValidationException extends ChatBusinessException {
    public ChatMessageValidationException() {
        super(BIND_EXCEPTION);
    }
}
