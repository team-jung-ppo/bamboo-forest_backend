package org.jungppo.bambooforest.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberDeleteEvent {
    private final Long memberId;
}
