package org.jungppo.bambooforest.global.oauth2.presentation;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;
import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_REFRESH_TOKEN_SERVICE;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.global.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository;
import org.jungppo.bambooforest.global.util.CookieUtils;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtAccessTokenService;
    private final JwtService jwtRefreshTokenService;

    public CustomOAuth2LoginSuccessHandler(
            final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
            final RefreshTokenService refreshTokenService,
            @Qualifier(JWT_ACCESS_TOKEN_SERVICE) final JwtService jwtAccessTokenService,
            @Qualifier(JWT_REFRESH_TOKEN_SERVICE) final JwtService jwtRefreshTokenService) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtAccessTokenService = jwtAccessTokenService;
        this.jwtRefreshTokenService = jwtRefreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final String targetUrl = determineTargetUrl(request, response, authentication);
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {
        final String targetUrl = getTargetUrl(request);
        final CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        final Long id = customOAuth2User.getId();
        final RoleType roleType = customOAuth2User.getRoleType();
        final OAuth2Type oAuth2Type = customOAuth2User.getOAuth2Type();
        final String accessToken = jwtAccessTokenService.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));
        final String refreshToken = jwtRefreshTokenService.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));
        refreshTokenService.saveOrUpdateRefreshToken(id, refreshToken);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    private String getTargetUrl(final HttpServletRequest request) {
        return CookieUtils.getCookie(request,
                        HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request, final HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.deleteAuthorizationCookies(request, response);
    }
}
