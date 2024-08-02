package org.jungppo.bambooforest.chat.handler;

import org.jungppo.bambooforest.chat.dto.ChatMessageDto;
import org.jungppo.bambooforest.chat.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketServerHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    // websocket handshake 완료되어 연결이 완료 되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.putIfAbsent(session.getId(), session);
    }

    // websocket 메세지를 받았을 때 호출
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        try {
            // 메시지 페이로드에서 sender 정보 추출
            ChatMessageDto chatMessageDto = parseMessage(payload);
            // 메시지 타입에 따른 처리
            handleMessage(session, chatMessageDto, payload);
        } catch (Exception e) {
            sendMessageToUser(session, "Error processing message");
        }
    }

    private ChatMessageDto parseMessage(String payload) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(payload);
        return ChatMessageDto.from(jsonNode);
    }

    private void handleMessage(WebSocketSession session, ChatMessageDto chatMessageDto, String payload) throws Exception {
        switch (chatMessageDto.getType()) {
            case ENTER:
                break;
            case TALK:
                String chatbotResponse = chatService.handleMessage(chatMessageDto, payload, session);
                sendMessageToUser(session, chatbotResponse);
                break;
            case LEAVE:
                session.close();
                break;
        }
    }

    // websocket 오류가 발생했을 때 호출
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session.getId());
    }

    // websocket 연결이 종료되었을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        // chatService.batchSaveMessages();
    }

    // 부분 메시지를 지원하지 않음
    @Override
    public boolean supportsPartialMessages() {
        return false; 
    }

    private void sendMessageToUser(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("Failed to send message to user, session id={}, error={}", session.getId(), e.getMessage());
        }
    }
}
