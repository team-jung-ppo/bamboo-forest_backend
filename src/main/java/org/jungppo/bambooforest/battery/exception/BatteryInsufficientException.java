package org.jungppo.bambooforest.battery.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.BATTERY_INSUFFICIENT_EXCEPTION;

public class BatteryInsufficientException extends BatteryBusinessException {
    public BatteryInsufficientException() {
        super(BATTERY_INSUFFICIENT_EXCEPTION);
    }
}
