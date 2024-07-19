package org.jungppo.bambooforest.chat.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.ROOM_NOT_FOUND_EXCEPTION;

public class RoomNotFoundException extends ChatBusinessException {
    public RoomNotFoundException() {
        super(ROOM_NOT_FOUND_EXCEPTION);
    }
}
