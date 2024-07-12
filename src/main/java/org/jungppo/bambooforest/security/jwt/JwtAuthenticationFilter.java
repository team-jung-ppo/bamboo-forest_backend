package org.jungppo.bambooforest.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.response.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.jungppo.bambooforest.response.ResponseUtil.createFailureResponse;
import static org.jungppo.bambooforest.response.exception.common.ExceptionType.JWT_EXPIRED_EXCEPTION;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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

    private Optional<String> getToken(HttpServletRequest request){
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)){
            return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleExpiredJwtException(HttpServletResponse response) throws IOException {
        response.setStatus(JWT_EXPIRED_EXCEPTION.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(convertToJson(createFailureResponse(JWT_EXPIRED_EXCEPTION)));
    }

    private String convertToJson(ResponseBody<Void> response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(response);
    }
}
