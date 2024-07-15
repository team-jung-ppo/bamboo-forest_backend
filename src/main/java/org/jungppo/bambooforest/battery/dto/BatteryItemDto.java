package org.jungppo.bambooforest.battery.dto;

import java.math.BigDecimal;

import org.jungppo.bambooforest.battery.domain.BatteryItem;

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
