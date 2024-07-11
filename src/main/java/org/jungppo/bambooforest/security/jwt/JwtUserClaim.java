package org.jungppo.bambooforest.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jungppo.bambooforest.entity.type.RoleType;

@Getter
@AllArgsConstructor
public class JwtUserClaim {
	private final Long userId;
	private final RoleType roleType;
}
