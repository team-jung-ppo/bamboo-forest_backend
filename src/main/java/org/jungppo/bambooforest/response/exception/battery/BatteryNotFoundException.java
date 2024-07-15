package org.jungppo.bambooforest.response.exception.battery;

import org.jungppo.bambooforest.response.exception.common.ExceptionType;

public class BatteryNotFoundException extends BatteryBusinessException {
	public BatteryNotFoundException() {
		super(ExceptionType.BATTERY_NOT_FOUND_EXCEPTION);
	}
}
