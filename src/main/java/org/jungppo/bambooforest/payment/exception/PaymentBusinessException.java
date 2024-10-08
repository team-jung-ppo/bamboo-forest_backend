package org.jungppo.bambooforest.payment.exception;

import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public abstract class PaymentBusinessException extends BusinessException {

    public PaymentBusinessException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

    public PaymentBusinessException(final ExceptionType exceptionType, final Throwable cause) {
        super(exceptionType, cause);
    }
}
