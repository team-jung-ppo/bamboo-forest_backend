package org.jungppo.bambooforest.response.exception.payment;

import org.jungppo.bambooforest.response.exception.common.ExceptionType;

public class PaymentNotFoundException extends PaymentBusinessException {
	public PaymentNotFoundException() {
		super(ExceptionType.PAYMENT_NOT_FOUND_EXCEPTION);
	}
}
