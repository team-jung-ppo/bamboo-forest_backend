package org.jungppo.bambooforest.member.fixture;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;
import static org.jungppo.bambooforest.member.domain.entity.RoleType.ROLE_USER;
import static org.jungppo.bambooforest.util.ReflectionUtils.setField;

import org.jungppo.bambooforest.member.domain.entity.MemberEntity;

public class MemberEntityFixture {

    public static final MemberEntity MEMBER_ENTITY;

    static {
        MEMBER_ENTITY = MemberEntity.of(
                "Test User",
                OAUTH2_KAKAO,
                "testuser",
                "https://example.com/profile.jpg",
                ROLE_USER
        );
        setField(MEMBER_ENTITY, "id", 1L);
    }
}
