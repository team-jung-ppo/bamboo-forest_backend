package org.jungppo.bambooforest.member.exception;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.MEMBER_NOT_FOUND_EXCEPTION;

public class MemberNotFoundException extends MemberBusinessException {
    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND_EXCEPTION);
    }
}
