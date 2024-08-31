package org.jungppo.bambooforest.battery.fixture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.battery.dto.BatteryItemDto;

public class BatteryItemDtoFixture {

    public static BatteryItemDto createBatteryItemDto(final BatteryItem batteryItem) {
        return BatteryItemDto.from(batteryItem);
    }

    public static List<BatteryItemDto> createBatteryItemDtos() {
        return Stream.of(BatteryItem.values())
                .map(BatteryItemDtoFixture::createBatteryItemDto)
                .collect(Collectors.toList());
    }
}
