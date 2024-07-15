package org.jungppo.bambooforest.payment.exception;

import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public class PaymentFailureException extends PaymentBusinessException {
    public PaymentFailureException() {
        super(ExceptionType.PAYMENT_FAILURE_EXCEPTION);
    }
}
