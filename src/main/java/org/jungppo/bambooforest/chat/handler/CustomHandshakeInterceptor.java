package org.jungppo.bambooforest.chat.handler;

import java.util.Map;
import java.util.Optional;

import org.jungppo.bambooforest.chat.exception.MissingRequiredAttributesException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor{
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Optional<String> roomId = getHeader(request, "roomId");
        Optional<String> memberId = getHeader(request, "memberId");

        
        if (roomId.isEmpty() || memberId.isEmpty()) {
            throw new MissingRequiredAttributesException();
        }
        
        attributes.put("roomId", roomId.get());
        attributes.put("memberId", memberId.get());

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }

    private Optional<String> getHeader(ServerHttpRequest request, String headerName) {
        return Optional.ofNullable(request.getHeaders().getFirst(headerName));
    }
}
