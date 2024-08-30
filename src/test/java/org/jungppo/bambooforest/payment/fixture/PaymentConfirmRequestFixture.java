package org.jungppo.bambooforest.payment.fixture;

import static org.jungppo.bambooforest.payment.fixture.PaymentSetupResponseFixture.MEDIUM_BATTERY_SETUP_RESPONSE;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupResponseFixture.SMALL_BATTERY_SETUP_RESPONSE;

import org.jungppo.bambooforest.member.dto.PaymentConfirmRequest;

public class PaymentConfirmRequestFixture {

    public static final PaymentConfirmRequest PAYMENT_CONFIRM_REQUEST = new PaymentConfirmRequest(
            "validPaymentKey",
            SMALL_BATTERY_SETUP_RESPONSE.getOrderId(),
            SMALL_BATTERY_SETUP_RESPONSE.getAmount()
    );

    public static final PaymentConfirmRequest INVALID_PAYMENT_CONFIRM_REQUEST = new PaymentConfirmRequest(
            "invalidPaymentKey",
            MEDIUM_BATTERY_SETUP_RESPONSE.getOrderId(),
            MEDIUM_BATTERY_SETUP_RESPONSE.getAmount()
    );
}
