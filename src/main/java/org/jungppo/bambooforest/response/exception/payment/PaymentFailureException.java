package org.jungppo.bambooforest.response.exception.payment;

import org.jungppo.bambooforest.response.exception.common.ExceptionType;

public class PaymentFailureException extends PaymentBusinessException {
	public PaymentFailureException() {
		super(ExceptionType.PAYMENT_FAILURE_EXCEPTION);
	}
}
