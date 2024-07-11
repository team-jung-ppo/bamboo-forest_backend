package org.jungppo.bambooforest.response.exception.member;

import org.webppo.clubcommunity_backend.response.exception.common.ExceptionType;

public class MemberNotFoundException extends MemberBusinessException {

    public MemberNotFoundException() {
        super(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION);
    }
}
