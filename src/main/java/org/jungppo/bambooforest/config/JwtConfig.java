package org.jungppo.bambooforest.config;

import org.jungppo.bambooforest.security.jwt.JwtProperties;
import org.jungppo.bambooforest.util.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
	public static final String JWT_ACCESS_TOKEN_UTILS = "jwtAccessTokenUtils";
	public static final String JWT_REFRESH_TOKEN_UTILS = "jwtRefreshTokenUtils";

	@Bean
	@Qualifier(JWT_ACCESS_TOKEN_UTILS)
	public JwtUtils jwtAccessTokenUtils(JwtProperties jwtProperties) {
		JwtProperties.AccessToken accessToken = jwtProperties.getAccessToken();
		return new JwtUtils(accessToken.getSecretKey(), accessToken.getExpireIn());
	}

	@Bean
	@Qualifier(JWT_REFRESH_TOKEN_UTILS)
	public JwtUtils jwtRefreshTokenUtils(JwtProperties jwtProperties) {
		JwtProperties.RefreshToken refreshToken = jwtProperties.getRefreshToken();
		return new JwtUtils(refreshToken.getSecretKey(), refreshToken.getExpireIn());
	}
}
