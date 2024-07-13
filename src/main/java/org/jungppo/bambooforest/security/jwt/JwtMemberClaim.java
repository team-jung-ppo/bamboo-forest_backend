package org.jungppo.bambooforest.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jungppo.bambooforest.entity.type.RoleType;

@Getter
@AllArgsConstructor
public class JwtMemberClaim {
	private final Long id;
	private final RoleType role;
	private final String registrationId;
}
