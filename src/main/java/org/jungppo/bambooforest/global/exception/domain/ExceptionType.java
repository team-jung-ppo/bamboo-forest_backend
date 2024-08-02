package org.jungppo.bambooforest.global.exception.domain;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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
    BATTERY_INSUFFICIENT_EXCEPTION(BAD_REQUEST, "E011", "Not enough batteries to purchase the chatbot."),
    PAYMENT_NOT_FOUND_EXCEPTION(NOT_FOUND, "E012", "The specified payment could not be found."),
    PAYMENT_FAILURE_EXCEPTION(BAD_REQUEST, "E013", "Failed to processing payment."),
    PAYMENT_PENDING_EXCEPTION(BAD_REQUEST, "E014", "The payment is still pending."),
    CHATBOT_NOT_FOUND_EXCEPTION(NOT_FOUND, "E015", "The specified chatBot item could not be found."),
    CHATBOT_ALREADY_OWNED_EXCEPTION(BAD_REQUEST, "E016", "The specified chatBot item is already owned by the user."),
    CHATBOT_PURCHASE_NOT_FOUND_EXCEPTION(NOT_FOUND, "E017", "The specified chatBot purchase could not be found."),
    ROOM_NOT_FOUND_EXCEPTION(NOT_FOUND, "E018", "The specified room could not be found."),
    CHATBOT_NOT_AVAILABLE_EXCEPTION(BAD_REQUEST, "E019", "The specified chatBot item is not available for purchase."),
    CHATBOT_TYPE_MISMATCH_EXCEPTION(BAD_REQUEST, "E020", "The specified chatBot type does not match the purchased chatBot type."),
    SOCKET_MISSING_REQUIRED_ATTRIBUTES(BAD_REQUEST, "E021", "The specified Missing required attributes"),
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
