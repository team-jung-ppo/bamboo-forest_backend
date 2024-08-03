package org.jungppo.bambooforest.chat.handler;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;

import java.util.Map;

import org.jungppo.bambooforest.chat.exception.InvalidJwtFormatException;
import org.jungppo.bambooforest.chat.exception.JwtValidationException;
import org.jungppo.bambooforest.chat.exception.MissingJwtTokenException;
import org.jungppo.bambooforest.chat.exception.TokenExpiredException;
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
        String token = headers.getFirst("Authorization");

        if (token == null) throw new MissingJwtTokenException();

        if(!token.startsWith("Bearer ")) throw new InvalidJwtFormatException();

        token = token.substring(7); // "Bearer " 제거

        try {
            jwtService.parseToken(token); // 토큰 검증
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new JwtValidationException();
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
        // 후처리
    }
    
}
