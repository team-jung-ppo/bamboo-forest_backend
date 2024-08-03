package org.jungppo.bambooforest.chat.handler;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;

import java.util.Map;
import java.util.Optional;

import org.jungppo.bambooforest.global.jwt.exception.AuthenticationEntryPointException;
import org.jungppo.bambooforest.global.jwt.exception.TokenExpiredException;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;

    public JwtHandshakeInterceptor(@Qualifier(JWT_ACCESS_TOKEN_SERVICE) JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        HttpHeaders headers = request.getHeaders();
        return Optional.ofNullable(headers.getFirst("Authorization"))
                       .filter(token -> token.startsWith("Bearer "))
                       .map(token -> token.substring(7))
                       .map(this::validateToken)
                       .orElseThrow(AuthenticationEntryPointException::new);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
        // 후처리
    }

    private boolean validateToken(String token) {
        try {
            jwtService.parseToken(token); // 토큰 검증
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new AuthenticationEntryPointException();
        }
    }
    
}
