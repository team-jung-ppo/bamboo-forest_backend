package org.jungppo.bambooforest.entity.battery;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BatteryItem {

	BATTERY_ONE("배터리 3개", BigDecimal.valueOf(3000), 3),
	BATTERY_TWO("배터리 5개", BigDecimal.valueOf(5000), 5),
	BATTERY_THREE("배터리 10개", BigDecimal.valueOf(10000), 10),
	BATTERY_FOUR("배터리 20개", BigDecimal.valueOf(20000), 20),
	BATTERY_FIVE("배터리 50개", BigDecimal.valueOf(50000), 50);

	private final String name;
	private final BigDecimal price;
	private final int count;

	private static final Map<String, BatteryItem> BATTERY_MAP;

	static {
		BATTERY_MAP = Collections.unmodifiableMap(Arrays.stream(BatteryItem.values())
			.collect(Collectors.toMap(BatteryItem::getName, Function.identity())));
	}

	public static BatteryItem findByName(String name) {
		return Optional.ofNullable(BATTERY_MAP.get(name))
			.orElseThrow(() -> new IllegalArgumentException("Invalid battery name: " + name));
	}
}
