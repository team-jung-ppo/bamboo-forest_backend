package org.jungppo.bambooforest.member.fixture;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;
import static org.jungppo.bambooforest.member.domain.entity.RoleType.ROLE_USER;

import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.util.ReflectionUtils;

public class CustomOAuth2UserFixture {

    public static final CustomOAuth2User CUSTOM_OAUTH2_USER;

    static {
        CUSTOM_OAUTH2_USER = new CustomOAuth2User(
                MemberEntityFixture.MEMBER_ENTITY.getId(),
                ROLE_USER,
                OAUTH2_KAKAO
        );
        ReflectionUtils.setField(CUSTOM_OAUTH2_USER, "id", MemberEntityFixture.MEMBER_ENTITY.getId());
    }
}
