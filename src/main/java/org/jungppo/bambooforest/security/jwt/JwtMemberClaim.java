package org.jungppo.bambooforest.security.jwt;

import org.jungppo.bambooforest.entity.type.OAuth2Type;
import org.jungppo.bambooforest.entity.type.RoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtMemberClaim {
	private final Long id;
	private final RoleType roleType;
	private final OAuth2Type oAuth2Type;
}
