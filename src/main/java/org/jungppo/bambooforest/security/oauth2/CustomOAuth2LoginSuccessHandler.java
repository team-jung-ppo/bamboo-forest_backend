package org.jungppo.bambooforest.security.oauth2;

import static org.jungppo.bambooforest.config.JwtConfig.*;
import static org.jungppo.bambooforest.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.*;

import java.io.IOException;

import org.jungppo.bambooforest.entity.type.OAuth2Type;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.member.service.RefreshTokenService;
import org.jungppo.bambooforest.security.jwt.JwtMemberClaim;
import org.jungppo.bambooforest.util.CookieUtils;
import org.jungppo.bambooforest.util.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
	private final RefreshTokenService refreshTokenService;
	private final JwtUtils jwtAccessTokenUtils;
	private final JwtUtils jwtRefreshTokenUtils;

	public CustomOAuth2LoginSuccessHandler(
		HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
		RefreshTokenService refreshTokenService,
		@Qualifier(JWT_ACCESS_TOKEN_UTILS) JwtUtils jwtAccessTokenUtils,
		@Qualifier(JWT_REFRESH_TOKEN_UTILS) JwtUtils jwtRefreshTokenUtils) {
		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
		this.refreshTokenService = refreshTokenService;
		this.jwtAccessTokenUtils = jwtAccessTokenUtils;
		this.jwtRefreshTokenUtils = jwtRefreshTokenUtils;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(request, response, authentication);
		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		String targetUrl = getTargetUrl(request);
		CustomOAuth2User customOAuth2User = (CustomOAuth2User)authentication.getPrincipal();
		Long id = customOAuth2User.getId();
		RoleType roleType = customOAuth2User.getRoleType();
		OAuth2Type oAuth2Type = customOAuth2User.getOAuth2Type();
		String accessToken = jwtAccessTokenUtils.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));
		String refreshToken = jwtRefreshTokenUtils.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));
		refreshTokenService.saveOrUpdateRefreshToken(id, refreshToken);

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

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.deleteAuthorizationCookies(request, response);
	}
}
