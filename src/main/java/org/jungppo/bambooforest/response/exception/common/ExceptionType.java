package org.jungppo.bambooforest.response.exception.common;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {
	EXCEPTION(INTERNAL_SERVER_ERROR, "E001", "An unexpected error has occurred."),
	AUTHENTICATION_ENTRY_POINT_EXCEPTION(UNAUTHORIZED, "E002", "Authentication is required to access this resource."),
	JWT_EXPIRED_EXCEPTION(UNAUTHORIZED, "E003", "The JWT token has expired."),
	REFRESH_TOKEN_FAILURE_EXCEPTION(BAD_REQUEST, "E004", "Failed to reissue the refresh token."),
	ACCESS_DENIED_EXCEPTION(FORBIDDEN, "E005", "You do not have permission to access this resource."),
	BIND_EXCEPTION(BAD_REQUEST, "E006", "There was an error with the request binding."),
	OAUTH2_LOGIN_FAILURE_EXCEPTION(UNAUTHORIZED, "E007", "OAuth2 login failed. Please try again."),
	MEMBER_NOT_FOUND_EXCEPTION(NOT_FOUND, "E008", "The specified member could not be found."),
	UNSUPPORTED_OAUTH2_EXCEPTION(INTERNAL_SERVER_ERROR, "E009", "Unsupported OAuth2 provider."),
	BATTERY_NOT_FOUND_EXCEPTION(NOT_FOUND, "E010", "The specified battery item could not be found."),
	PAYMENT_NOT_FOUND_EXCEPTION(NOT_FOUND, "E011", "The specified payment could not be found."),
	;
	private final HttpStatus status;
	private final String code;
	private final String message;
}
