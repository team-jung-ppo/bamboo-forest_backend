package org.jungppo.bambooforest.payment.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import org.jungppo.bambooforest.member.dto.PaymentSetupResponse;

public class PaymentSetupResponseFixture {

    public static final PaymentSetupResponse SMALL_BATTERY_SETUP_RESPONSE = new PaymentSetupResponse(
            UUID.randomUUID(),
            new BigDecimal("50.00")
    );

    public static final PaymentSetupResponse MEDIUM_BATTERY_SETUP_RESPONSE = new PaymentSetupResponse(
            UUID.randomUUID(),
            new BigDecimal("75.00")
    );

    public static final PaymentSetupResponse LARGE_BATTERY_SETUP_RESPONSE = new PaymentSetupResponse(
            UUID.randomUUID(),
            new BigDecimal("100.00")
    );
}
