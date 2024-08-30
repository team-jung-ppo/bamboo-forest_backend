package org.jungppo.bambooforest.global.jwt.fixture;

import static org.jungppo.bambooforest.global.oauth2.fixture.CustomOAuth2UserFixture.CUSTOM_OAUTH2_USER;

import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;

public class JwtMemberClaimFixture {

    public static final JwtMemberClaim JWT_MEMBER_CLAIM = new JwtMemberClaim(
            CUSTOM_OAUTH2_USER.getId(),
            RoleType.ROLE_USER,
            OAuth2Type.OAUTH2_GITHUB
    );
}
