package org.jungppo.bambooforest.member.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.UNSUPPORTED_OAUTH2_EXCEPTION;

public class UnsupportedOAuth2Exception extends MemberBusinessException {
    public UnsupportedOAuth2Exception() {
        super(UNSUPPORTED_OAUTH2_EXCEPTION);
    }
}
