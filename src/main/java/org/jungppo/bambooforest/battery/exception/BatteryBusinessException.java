package org.jungppo.bambooforest.battery.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class BatteryBusinessException extends BusinessException {
    public BatteryBusinessException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public BatteryBusinessException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
