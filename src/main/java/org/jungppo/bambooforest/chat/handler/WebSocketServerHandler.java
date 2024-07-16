package org.jungppo.bambooforest.chat.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketServerHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // websocket handshake 완료되어 연결이 완료 되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection established, session id={}, session.getId");
        sessions.putIfAbsent(session.getId(), session);
    }

    // websocket 메세지를 받았을 때 호출
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("received message, session id={}, message={}", session.getId(), payload);
    }

    // websocket 오류가 발생했을 때 호출
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("session transport exception, session id={}, error={}", session.getId(), exception.getMessage());
        sessions.remove(session.getId());
    }

    // websocket 연결이 종료되었을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection closed, session id={}, status={}", session.getId(), status);
        sessions.remove(session.getId());
    }

    // 부분 메시지를 지원하지 않음
    @Override
    public boolean supportsPartialMessages() {
        return false; 
    }
}
