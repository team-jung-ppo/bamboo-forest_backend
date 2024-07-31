package org.jungppo.bambooforest.global.exception.service;

import io.micrometer.common.lang.Nullable;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.exception.domain.BusinessException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;
import org.jungppo.bambooforest.global.exception.dto.ExceptionResponse;
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
        log.warn(e.getMessage(), e);
        final String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(ExceptionType.BIND_EXCEPTION.getStatus())
                .body(new ExceptionResponse(ExceptionType.BIND_EXCEPTION.getCode(), message));
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers,
                                                          HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ProblemDetail problemDetail) {
            log.warn("ProblemDetail: {}", problemDetail);
            return ResponseEntity.status(statusCode)
                    .body(new ExceptionResponse(ExceptionType.EXCEPTION.getCode(), problemDetail.getDetail()));
        }
        log.error("Response body is not an instance of ProblemDetail. Body: {}", body);
        return ResponseEntity.status(ExceptionType.EXCEPTION.getStatus())
                .body(new ExceptionResponse(ExceptionType.EXCEPTION.getCode(), ExceptionType.EXCEPTION.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(e.getStatus())
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);// 처리되지 않은 모든 오류
        return ResponseEntity
                .status(ExceptionType.EXCEPTION.getStatus())
                .body(new ExceptionResponse(ExceptionType.EXCEPTION.getCode(), ExceptionType.EXCEPTION.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class) // @PreAuthorize으로 부터 발생하는 오류
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity
                .status(ExceptionType.ACCESS_DENIED_EXCEPTION.getStatus())
                .body(new ExceptionResponse(
                        ExceptionType.ACCESS_DENIED_EXCEPTION.getCode(),
                        ExceptionType.ACCESS_DENIED_EXCEPTION.getMessage()));
    }
}
