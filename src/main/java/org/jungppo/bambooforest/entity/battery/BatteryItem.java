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

	SMALL_BATTERY("작은 배터리", BigDecimal.valueOf(3000), 3),
	MEDIUM_BATTERY("중간 배터리", BigDecimal.valueOf(5000), 5),
	LARGE_BATTERY("큰 배터리", BigDecimal.valueOf(10000), 10),
	SMALL_BATTERY_PACK("작은 배터리 팩", BigDecimal.valueOf(20000), 20),
	MEDIUM_BATTERY_PACK("중간 배터리 팩", BigDecimal.valueOf(50000), 50),
	LARGE_BATTERY_PACK("큰 배터리 팩", BigDecimal.valueOf(100000), 100);

	private final String name;
	private final BigDecimal price;
	private final int count;

	private static final Map<String, BatteryItem> BATTERY_MAP;

	static {
		BATTERY_MAP = Collections.unmodifiableMap(Arrays.stream(BatteryItem.values())
			.collect(Collectors.toMap(BatteryItem::getName, Function.identity())));
	}

	public static Optional<BatteryItem> findByName(String name) {
		return Optional.ofNullable(BATTERY_MAP.get(name));
	}
}
