package org.jungppo.bambooforest.battery.exception;

import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public class BatteryNotFoundException extends BatteryBusinessException {
    public BatteryNotFoundException() {
        super(ExceptionType.BATTERY_NOT_FOUND_EXCEPTION);
    }
}
