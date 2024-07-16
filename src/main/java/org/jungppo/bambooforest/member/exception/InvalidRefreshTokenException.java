package org.jungppo.bambooforest.member.exception;

public class InvalidRefreshTokenException extends Exception {
    public InvalidRefreshTokenException(final String message) {
        super(message);
    }
}
