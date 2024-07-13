package org.jungppo.bambooforest.security.jwt;

import static org.jungppo.bambooforest.config.JwtConfig.*;

import org.jungppo.bambooforest.security.oauth2.CustomOAuth2User;
import org.jungppo.bambooforest.util.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider implements AuthenticationProvider {

	private final JwtUtils jwtAccessTokenUtils;

	public JwtProvider(@Qualifier(JWT_ACCESS_TOKEN_UTILS) JwtUtils jwtAccessTokenUtils) {
		this.jwtAccessTokenUtils = jwtAccessTokenUtils;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		String tokenValue = authentication.getCredentials().toString();
		try {
			JwtMemberClaim claims = jwtAccessTokenUtils.parseToken(tokenValue);
			CustomOAuth2User customOAuth2User = new CustomOAuth2User(claims.getId(), claims.getRoleType(),
				claims.getOAuth2Type());
			return new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(),
				customOAuth2User.getOAuth2Type().getRegistrationId());
		} catch (Exception e) {
			throw new AuthenticationException("JWT token error", e) {
			};
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
