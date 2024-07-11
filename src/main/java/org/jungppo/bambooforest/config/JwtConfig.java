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
		JwtProperties.AccessTokenProperties accessTokenProperties = jwtProperties.getAccessTokenProperties();
		return new JwtUtils(accessTokenProperties.getSecretKey(), accessTokenProperties.getExpireIn());
	}

	@Bean
	@Qualifier(JWT_REFRESH_TOKEN_UTILS)
	public JwtUtils jwtRefreshTokenUtils(JwtProperties jwtProperties) {
		JwtProperties.RefreshTokenProperties refreshTokenProperties = jwtProperties.getRefreshTokenProperties();
		return new JwtUtils(refreshTokenProperties.getSecretKey(), refreshTokenProperties.getExpireIn());
	}
}
