package org.jungppo.bambooforest.payment.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.PAYMENT_PENDING_EXCEPTION;

public class PaymentPendingException extends PaymentBusinessException {
    public PaymentPendingException() {
        super(PAYMENT_PENDING_EXCEPTION);
    }
}
