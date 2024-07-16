package org.jungppo.bambooforest.battery.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.BATTERY_NOT_FOUND_EXCEPTION;

public class BatteryNotFoundException extends BatteryBusinessException {
    public BatteryNotFoundException() {
        super(BATTERY_NOT_FOUND_EXCEPTION);
    }
}
