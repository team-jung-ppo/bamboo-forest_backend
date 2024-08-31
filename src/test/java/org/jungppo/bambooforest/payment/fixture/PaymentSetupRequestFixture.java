package org.jungppo.bambooforest.payment.fixture;

import org.jungppo.bambooforest.member.dto.PaymentSetupRequest;

public class PaymentSetupRequestFixture {

    public static PaymentSetupRequest createPaymentSetupRequest(final String batteryItemName) {
        return new PaymentSetupRequest(batteryItemName);
    }
}
