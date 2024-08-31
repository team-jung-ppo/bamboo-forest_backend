package org.jungppo.bambooforest.payment.fixture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.jungppo.bambooforest.battery.dto.BatteryItemDto;
import org.jungppo.bambooforest.member.dto.PaymentDto;

public class PaymentDtoFixture {

    public static PaymentDto createPaymentDto(final UUID id, final BatteryItemDto batteryItemDto,
                                              final BigDecimal amount) {
        return new PaymentDto(id, batteryItemDto, "Toss", amount, LocalDateTime.now());
    }
}
