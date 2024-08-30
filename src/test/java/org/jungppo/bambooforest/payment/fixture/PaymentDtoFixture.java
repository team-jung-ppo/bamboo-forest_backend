package org.jungppo.bambooforest.payment.fixture;

import static org.jungppo.bambooforest.battery.domain.BatteryItem.LARGE_BATTERY;
import static org.jungppo.bambooforest.battery.domain.BatteryItem.MEDIUM_BATTERY;
import static org.jungppo.bambooforest.battery.domain.BatteryItem.SMALL_BATTERY;

import java.time.LocalDateTime;
import java.util.UUID;
import org.jungppo.bambooforest.battery.fixture.BatteryItemDtoFixture;
import org.jungppo.bambooforest.member.dto.PaymentDto;

public class PaymentDtoFixture {

    public static final PaymentDto SMALL_BATTERY_PAYMENT_DTO = new PaymentDto(
            UUID.randomUUID(),
            BatteryItemDtoFixture.SMALL_BATTERY_DTO,
            "Toss",
            SMALL_BATTERY.getPrice(),
            LocalDateTime.now()
    );

    public static final PaymentDto MEDIUM_BATTERY_PAYMENT_DTO = new PaymentDto(
            UUID.randomUUID(),
            BatteryItemDtoFixture.MEDIUM_BATTERY_DTO,
            "Toss",
            MEDIUM_BATTERY.getPrice(),
            LocalDateTime.now()
    );

    public static final PaymentDto LARGE_BATTERY_PAYMENT_DTO = new PaymentDto(
            UUID.randomUUID(),
            BatteryItemDtoFixture.LARGE_BATTERY_DTO,
            "Toss",
            LARGE_BATTERY.getPrice(),
            LocalDateTime.now()
    );
}
