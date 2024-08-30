package org.jungppo.bambooforest.payment.fixture;

import static org.jungppo.bambooforest.battery.fixture.BatteryItemDtoFixture.LARGE_BATTERY_DTO;
import static org.jungppo.bambooforest.battery.fixture.BatteryItemDtoFixture.MEDIUM_BATTERY_DTO;
import static org.jungppo.bambooforest.battery.fixture.BatteryItemDtoFixture.SMALL_BATTERY_DTO;

import org.jungppo.bambooforest.member.dto.PaymentSetupRequest;

public class PaymentSetupRequestFixture {

    public static final PaymentSetupRequest SMALL_BATTERY_SETUP_REQUEST =
            new PaymentSetupRequest(SMALL_BATTERY_DTO.getName());

    public static final PaymentSetupRequest MEDIUM_BATTERY_SETUP_REQUEST =
            new PaymentSetupRequest(MEDIUM_BATTERY_DTO.getName());

    public static final PaymentSetupRequest LARGE_BATTERY_SETUP_REQUEST =
            new PaymentSetupRequest(LARGE_BATTERY_DTO.getName());

    public static final PaymentSetupRequest INVALID_BATTERY_SETUP_REQUEST =
            new PaymentSetupRequest("InvalidBatteryItemName");
}
