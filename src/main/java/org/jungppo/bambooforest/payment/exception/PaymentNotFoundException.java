package org.jungppo.bambooforest.payment.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.PAYMENT_NOT_FOUND_EXCEPTION;

public class PaymentNotFoundException extends PaymentBusinessException {
    public PaymentNotFoundException() {
        super(PAYMENT_NOT_FOUND_EXCEPTION);
    }
}
