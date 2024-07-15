package org.jungppo.bambooforest.dto.battery;

import java.math.BigDecimal;

import org.jungppo.bambooforest.entity.battery.BatteryItem;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatteryItemDto {
	private String name;
	private BigDecimal price;
	private int count;

	public BatteryItemDto(BatteryItem batteryItem) {
		this.name = batteryItem.getName();
		this.price = batteryItem.getPrice();
		this.count = batteryItem.getCount();
	}
}
