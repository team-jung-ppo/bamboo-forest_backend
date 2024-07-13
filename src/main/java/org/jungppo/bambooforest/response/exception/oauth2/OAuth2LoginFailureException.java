package org.jungppo.bambooforest.response.exception.oauth2;
import org.springframework.security.core.AuthenticationException;

public class OAuth2LoginFailureException extends AuthenticationException {
    public OAuth2LoginFailureException(String message) {
        super(message);
    }
}
