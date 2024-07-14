package org.jungppo.bambooforest.response.exception.payment;

import org.jungppo.bambooforest.response.exception.common.ExceptionType;
import org.springframework.web.server.ResponseStatusException;

public abstract class PaymentBusinessException extends ResponseStatusException {
	public PaymentBusinessException(ExceptionType exceptionType) {
		super(exceptionType.getStatus(), exceptionType.getMessage());
	}

	public PaymentBusinessException(ExceptionType exceptionType, String reason) {
		super(exceptionType.getStatus(), reason);
	}
}
