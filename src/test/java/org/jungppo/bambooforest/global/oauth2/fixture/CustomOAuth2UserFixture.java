package org.jungppo.bambooforest.global.oauth2.fixture;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;
import static org.jungppo.bambooforest.member.domain.entity.RoleType.ROLE_USER;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.MEMBER_ENTITY;
import static org.jungppo.bambooforest.util.ReflectionUtils.setField;

import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;

public class CustomOAuth2UserFixture {

    public static final CustomOAuth2User CUSTOM_OAUTH2_USER;

    static {
        CUSTOM_OAUTH2_USER = new CustomOAuth2User(
                MEMBER_ENTITY.getId(),
                ROLE_USER,
                OAUTH2_KAKAO
        );
        setField(CUSTOM_OAUTH2_USER, "id", MEMBER_ENTITY.getId());
    }
}
