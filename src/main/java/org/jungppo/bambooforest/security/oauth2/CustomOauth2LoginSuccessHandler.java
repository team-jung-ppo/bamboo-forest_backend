package org.jungppo.bambooforest.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.entity.member.RefreshTokenEntity;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.repository.member.RefreshTokenRepository;
import org.jungppo.bambooforest.security.jwt.JwtMemberClaim;
import org.jungppo.bambooforest.util.CookieUtils;
import org.jungppo.bambooforest.util.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;

import static org.jungppo.bambooforest.config.JwtConfig.JWT_ACCESS_TOKEN_UTILS;
import static org.jungppo.bambooforest.config.JwtConfig.JWT_REFRESH_TOKEN_UTILS;
import static org.jungppo.bambooforest.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
public class CustomOauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtAccessTokenUtils;
    private final JwtUtils jwtRefreshTokenUtils;

    public CustomOauth2LoginSuccessHandler(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
                                           RefreshTokenRepository refreshTokenRepository,
                                           @Qualifier(JWT_ACCESS_TOKEN_UTILS) JwtUtils jwtAccessTokenUtils,
                                           @Qualifier(JWT_REFRESH_TOKEN_UTILS) JwtUtils jwtRefreshTokenUtils) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
        this.jwtRefreshTokenUtils = jwtRefreshTokenUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String targetUrl = getTargetUrl(request);
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long id = customOAuth2User.getId();
        RoleType role = customOAuth2User.getRoleType();
        String registrationId = customOAuth2User.getRegistrationId();
        String accessToken = jwtAccessTokenUtils.createToken(new JwtMemberClaim(id, role, registrationId));
        String refreshToken = jwtRefreshTokenUtils.createToken(new JwtMemberClaim(id, role, registrationId));
        saveOrUpdateRefreshToken(id, refreshToken);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();
    }

    private String getTargetUrl(HttpServletRequest request) {
        return CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());
    }

    /**
     * RefreshTokenEntity의 ID는 UserID와 동일함.
     * 동일한 ID를 가진 엔티티가 존재하면, save 메서드는 엔티티를 업데이트함.
     * 동일한 ID를 가진 엔티티가 존재하지 않으면, save 메서드는 엔티티를 저장함.
     */
    private void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(userId)
                .value(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.deleteAuthorizationCookies(request, response);
    }
}
