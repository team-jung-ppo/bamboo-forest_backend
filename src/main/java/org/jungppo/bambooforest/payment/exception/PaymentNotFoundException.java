package org.jungppo.bambooforest.payment.exception;

import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public class PaymentNotFoundException extends PaymentBusinessException {
    public PaymentNotFoundException() {
        super(ExceptionType.PAYMENT_NOT_FOUND_EXCEPTION);
    }
}
