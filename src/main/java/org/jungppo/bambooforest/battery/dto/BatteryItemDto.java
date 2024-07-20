package org.jungppo.bambooforest.battery.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.battery.domain.BatteryItem;

@Getter
@RequiredArgsConstructor
public class BatteryItemDto {
    private final String name;
    private final BigDecimal price;
    private final int count;

    public static BatteryItemDto from(final BatteryItem batteryItem) {
        return new BatteryItemDto(
                batteryItem.getName(),
                batteryItem.getPrice(),
                batteryItem.getCount()
        );
    }
}
