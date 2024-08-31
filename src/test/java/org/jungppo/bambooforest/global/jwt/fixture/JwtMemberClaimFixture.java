package org.jungppo.bambooforest.global.jwt.fixture;

import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;

public class JwtMemberClaimFixture {

    public static JwtMemberClaim createJwtMemberClaim(final Long userId, RoleType roleType,
                                                      final OAuth2Type oAuth2Type) {
        return new JwtMemberClaim(userId, roleType, oAuth2Type);
    }
}
