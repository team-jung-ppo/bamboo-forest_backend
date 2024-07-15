package org.jungppo.bambooforest.global.jwt.domain;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;

import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider implements AuthenticationProvider {

    private final JwtService jwtAccessTokenService;

    public JwtProvider(@Qualifier(JWT_ACCESS_TOKEN_SERVICE) JwtService jwtAccessTokenService) {
        this.jwtAccessTokenService = jwtAccessTokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String tokenValue = authentication.getCredentials().toString();
        try {
            final JwtMemberClaim claims = jwtAccessTokenService.parseToken(tokenValue);
            final CustomOAuth2User customOAuth2User = new CustomOAuth2User(claims.getId(), claims.getRoleType(),
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
        return JwtMemberClaim.JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
