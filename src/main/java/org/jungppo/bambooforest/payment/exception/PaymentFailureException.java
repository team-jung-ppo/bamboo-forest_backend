package org.jungppo.bambooforest.payment.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.PAYMENT_FAILURE_EXCEPTION;

public class PaymentFailureException extends PaymentBusinessException {
    public PaymentFailureException() {
        super(PAYMENT_FAILURE_EXCEPTION);
    }
}
