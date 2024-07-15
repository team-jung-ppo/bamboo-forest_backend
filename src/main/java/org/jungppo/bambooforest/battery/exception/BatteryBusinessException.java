package org.jungppo.bambooforest.battery.exception;

import org.jungppo.bambooforest.global.exception.domain.ExceptionType;
import org.springframework.web.server.ResponseStatusException;

public abstract class BatteryBusinessException extends ResponseStatusException {
    public BatteryBusinessException(ExceptionType exceptionType) {
        super(exceptionType.getStatus(), exceptionType.getMessage());
    }

    public BatteryBusinessException(ExceptionType exceptionType, String reason) {
        super(exceptionType.getStatus(), reason);
    }
}
