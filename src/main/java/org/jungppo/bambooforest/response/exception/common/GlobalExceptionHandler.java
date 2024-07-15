package org.jungppo.bambooforest.response.exception.common;

import static org.jungppo.bambooforest.response.exception.common.ExceptionType.*;

import java.util.stream.Collectors;

import org.jungppo.bambooforest.response.ExceptionResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		final MethodArgumentNotValidException e,
		final HttpHeaders headers,
		final HttpStatusCode status,
		final WebRequest request
	) {
		final String message = e.getBindingResult()
			.getAllErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.joining(", "));

		return ResponseEntity.status(BIND_EXCEPTION.getStatus())
			.body(new ExceptionResponse(BIND_EXCEPTION.getCode(), message));
	}

	@Override
	protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {
		if (body instanceof ProblemDetail problemDetail) {
			return ResponseEntity.status(statusCode)
				.body(new ExceptionResponse(EXCEPTION.getCode(), problemDetail.getDetail()));
		}
		log.warn("Response body is not an instance of ProblemDetail. Body: {}", body);
		return ResponseEntity.status(EXCEPTION.getStatus())
			.body(new ExceptionResponse(EXCEPTION.getCode(), EXCEPTION.getMessage()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
		ExceptionType exceptionType = e.getExceptionType();
		return ResponseEntity.status(exceptionType.getStatus())
			.body(new ExceptionResponse(exceptionType.getCode(), exceptionType.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception e) {  // 처리되지 않은 모든 오류
		log.error("Exception Message: {} ", e.getMessage());
		return ResponseEntity
			.status(EXCEPTION.getStatus())
			.body(new ExceptionResponse(EXCEPTION.getCode(), EXCEPTION.getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class) // @PreAuthorize으로 부터 발생하는 오류
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e) {
		return ResponseEntity
			.status(ACCESS_DENIED_EXCEPTION.getStatus())
			.body(new ExceptionResponse(ACCESS_DENIED_EXCEPTION.getCode(), ACCESS_DENIED_EXCEPTION.getMessage()));
	}
}
