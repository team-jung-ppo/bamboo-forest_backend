package org.jungppo.bambooforest.battery.dto;

import java.math.BigDecimal;
import lombok.Getter;
import org.jungppo.bambooforest.battery.domain.BatteryItem;

@Getter
public class BatteryItemDto {
    private final String name;
    private final BigDecimal price;
    private final int count;

    public BatteryItemDto(final BatteryItem batteryItem) {
        this.name = batteryItem.getName();
        this.price = batteryItem.getPrice();
        this.count = batteryItem.getCount();
    }
}
