package org.jungppo.bambooforest.chat.handler;

import java.util.Map;
import java.util.Optional;

import org.jungppo.bambooforest.chat.exception.MissingRequiredAttributesException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor{
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Optional<String> roomId = getHeader(request, "roomId");
        Optional<String> memberId = getHeader(request, "memberId");
        Optional<String> chatBotType = getHeader(request, "chatBotType");
        
        if (roomId.isEmpty() || memberId.isEmpty() || chatBotType.isEmpty()) {
            throw new MissingRequiredAttributesException();
        }
        
        attributes.put("roomId", roomId.get());
        attributes.put("memberId", memberId.get());
        attributes.put("chatBotType", chatBotType.get());

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
