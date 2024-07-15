package org.jungppo.bambooforest.global.oauth2.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2LoginFailureException extends AuthenticationException {
    public OAuth2LoginFailureException(String message) {
        super(message);
    }
}
