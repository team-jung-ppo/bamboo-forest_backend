package org.jungppo.bambooforest.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.util.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static org.jungppo.bambooforest.config.JwtConfig.JWT_ACCESS_TOKEN_UTILS;

@Slf4j
@Component
public class JwtProvider implements AuthenticationProvider {

	private final JwtUtils jwtAccessTokenUtils;

    public JwtProvider(@Qualifier(JWT_ACCESS_TOKEN_UTILS) JwtUtils jwtAccessTokenUtils) {
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
    }

    @Override
	public Authentication authenticate(Authentication authentication) {
		String tokenValue = getTokenValue(authentication);
		if (tokenValue == null) {
			return null;
		}
		try{
			JwtUserClaim claims = jwtAccessTokenUtils.parseToken(tokenValue);
			return new JwtAuthentication(claims.getUserId(), claims.getRoleType());
		} catch (ExpiredJwtException e) {  // 만료 정보만 반환. 이외에는 인증 실패
			throw e;
		} catch (Exception e) {
			return null;
		}
	}

	private String getTokenValue(Authentication authentication) {
		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
		return (String) jwtAuthenticationToken.getCredentials();
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
