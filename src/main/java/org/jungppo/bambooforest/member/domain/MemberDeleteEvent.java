package org.jungppo.bambooforest.member.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class MemberDeleteEvent {
    private final Long memberId;
}
