package org.jungppo.bambooforest.security.oauth2;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.jungppo.bambooforest.entity.type.OAuth2Type;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CustomOAuth2User implements OAuth2User, Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private final Long id;
	private final RoleType roleType;
	private final OAuth2Type oAuth2Type;

	@Override
	public String getName() {
		return id.toString();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(roleType.name()));
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}
}
