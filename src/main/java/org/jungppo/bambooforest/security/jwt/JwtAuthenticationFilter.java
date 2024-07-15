package org.jungppo.bambooforest.security.jwt;

import static org.jungppo.bambooforest.response.exception.common.ExceptionType.*;
import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.jungppo.bambooforest.response.ExceptionResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			getToken(request)
				.map(token -> authenticationManager.authenticate(new JwtAuthenticationToken(token)))
				.ifPresent(this::setAuthentication);
		} catch (AuthenticationException e) { // 보안 문제를 고려하여 만료 정보만 반환. 이외에는 인증 실패
			if (e.getCause() instanceof ExpiredJwtException) {
				handleExpiredJwtException(response);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	private Optional<String> getToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(AUTHORIZATION));
	}

	private void setAuthentication(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void handleExpiredJwtException(HttpServletResponse response) throws IOException {
		response.setStatus(JWT_EXPIRED_EXCEPTION.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter()
			.write(convertToJson(
				new ExceptionResponse(JWT_EXPIRED_EXCEPTION.getCode(), JWT_EXPIRED_EXCEPTION.getMessage())));
	}

	private String convertToJson(ExceptionResponse exceptionResponse) throws IOException {
		return objectMapper.writeValueAsString(exceptionResponse);
	}
}
