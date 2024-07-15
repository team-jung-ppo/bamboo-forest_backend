package org.jungppo.bambooforest.member.exception;

import org.jungppo.bambooforest.global.exception.domain.ExceptionType;

public class MemberNotFoundException extends MemberBusinessException {
    public MemberNotFoundException() {
        super(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION);
    }
}
