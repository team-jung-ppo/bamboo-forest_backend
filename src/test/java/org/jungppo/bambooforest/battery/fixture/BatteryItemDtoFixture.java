package org.jungppo.bambooforest.battery.fixture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.battery.dto.BatteryItemDto;

public class BatteryItemDtoFixture {

    public static final BatteryItemDto SMALL_BATTERY_DTO = BatteryItemDto.from(BatteryItem.SMALL_BATTERY);
    public static final BatteryItemDto MEDIUM_BATTERY_DTO = BatteryItemDto.from(BatteryItem.MEDIUM_BATTERY);
    public static final BatteryItemDto LARGE_BATTERY_DTO = BatteryItemDto.from(BatteryItem.LARGE_BATTERY);
    public static final BatteryItemDto SMALL_BATTERY_PACK_DTO = BatteryItemDto.from(BatteryItem.SMALL_BATTERY_PACK);
    public static final BatteryItemDto MEDIUM_BATTERY_PACK_DTO = BatteryItemDto.from(BatteryItem.MEDIUM_BATTERY_PACK);
    public static final BatteryItemDto LARGE_BATTERY_PACK_DTO = BatteryItemDto.from(BatteryItem.LARGE_BATTERY_PACK);

    public static final List<BatteryItemDto> BATTERY_ITEM_DTOS = Stream.of(BatteryItem.values())
            .map(BatteryItemDto::from)
            .collect(Collectors.toList());
}
