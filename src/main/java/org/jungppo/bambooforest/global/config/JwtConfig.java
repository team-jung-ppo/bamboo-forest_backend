package org.jungppo.bambooforest.global.config;

import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.global.jwt.settings.JwtProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    public static final String JWT_ACCESS_TOKEN_SERVICE = "jwtAccessTokenService";
    public static final String JWT_REFRESH_TOKEN_SERVICE = "jwtRefreshTokenService";

    @Bean
    @Qualifier(JWT_ACCESS_TOKEN_SERVICE)
    public JwtService jwtAccessTokenUtils(JwtProperties jwtProperties) {
        JwtProperties.AccessTokenProperties accessTokenProperties = jwtProperties.getAccessTokenProperties();
        return new JwtService(accessTokenProperties.getSecretKey(), accessTokenProperties.getExpireIn());
    }

    @Bean
    @Qualifier(JWT_REFRESH_TOKEN_SERVICE)
    public JwtService jwtRefreshTokenUtils(JwtProperties jwtProperties) {
        JwtProperties.RefreshTokenProperties refreshTokenProperties = jwtProperties.getRefreshTokenProperties();
        return new JwtService(refreshTokenProperties.getSecretKey(), refreshTokenProperties.getExpireIn());
    }
}
