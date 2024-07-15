package org.jungppo.bambooforest.member.exception;

import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public class UnsupportedOAuth2Exception extends MemberBusinessException {
    public UnsupportedOAuth2Exception() {
        super(ExceptionType.UNSUPPORTED_OAUTH2_EXCEPTION);
    }
}
