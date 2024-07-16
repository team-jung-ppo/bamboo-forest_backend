package org.jungppo.bambooforest.global.jwt.presentation;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.JWT_EXPIRED_EXCEPTION;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.exception.dto.ExceptionResponse;
import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        try {
            getToken(request)
                    .map(token -> authenticationManager.authenticate(new JwtMemberClaim.JwtAuthenticationToken(token)))
                    .ifPresent(this::setAuthentication);
        } catch (final AuthenticationException e) { // 보안 문제를 고려하여 만료 정보만 반환. 이외에는 인증 실패
            if (e.getCause() instanceof ExpiredJwtException) {
                handleExpiredJwtException(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getToken(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION));
    }

    private void setAuthentication(final Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleExpiredJwtException(final HttpServletResponse response) throws IOException {
        response.setStatus(JWT_EXPIRED_EXCEPTION.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(convertToJson(
                        new ExceptionResponse(JWT_EXPIRED_EXCEPTION.getCode(), JWT_EXPIRED_EXCEPTION.getMessage())));
    }

    private String convertToJson(final ExceptionResponse exceptionResponse) throws IOException {
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}
