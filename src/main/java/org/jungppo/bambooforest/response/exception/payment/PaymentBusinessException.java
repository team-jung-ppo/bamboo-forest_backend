package org.jungppo.bambooforest.response.exception.payment;

import org.jungppo.bambooforest.response.exception.common.BusinessException;
import org.jungppo.bambooforest.response.exception.common.ExceptionType;

public abstract class PaymentBusinessException extends BusinessException {

	public PaymentBusinessException(ExceptionType exceptionType) {
		super(exceptionType);
	}

	public PaymentBusinessException(ExceptionType exceptionType, Throwable cause) {
		super(exceptionType, cause);
	}
}
