package org.jungppo.bambooforest.chatbot.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.CHATBOT_ALREADY_OWNED_EXCEPTION;

public class ChatBotAlreadyOwnedException extends ChatBotBusinessException {
    public ChatBotAlreadyOwnedException() {
        super(CHATBOT_ALREADY_OWNED_EXCEPTION);
    }
}
