package org.jungppo.bambooforest.response.exception.member;

import org.jungppo.bambooforest.response.exception.common.ExceptionType;

public class MemberNotFoundException extends MemberBusinessException {
    public MemberNotFoundException() {
        super(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION);
    }
}
