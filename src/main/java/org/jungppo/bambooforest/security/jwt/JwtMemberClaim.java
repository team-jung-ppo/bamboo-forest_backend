package org.jungppo.bambooforest.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;

@Getter
@AllArgsConstructor
public class JwtMemberClaim {
    private final Long id;
    private final RoleType roleType;
    private final OAuth2Type oAuth2Type;
}
