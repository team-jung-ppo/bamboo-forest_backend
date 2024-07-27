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

    // websocket handshake 완료되어 연결이 완료 되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection established, session id={}", session.getId());
        sessions.putIfAbsent(session.getId(), session);
    }

    // websocket 메세지를 받았을 때 호출
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("received message, session id={}, message={}", session.getId(), payload);
        
        try {
            // 메시지 페이로드에서 sender 정보 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);
            String roomId = jsonNode.get("roomId").asText();
            String sender = jsonNode.get("sender").asText();
            String content = jsonNode.get("message").asText();
            String chatBotType = jsonNode.get("chatBotType").asText();
            ChatMessageDto.MessageType messageType = ChatMessageDto.MessageType.valueOf(jsonNode.get("type").asText());

            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .type(ChatMessageDto.MessageType.TALK)
                .roomId(roomId)
                .sender(sender)
                .content(content)
                .chatBotType(chatBotType)
                .build();

            // 메시지 타입에 따른 처리
            switch (messageType) {
                case ENTER:
                    log.info("User entered the room, session id={}, user={}", session.getId(), sender);
                    break;
                case TALK:
                    // 챗봇에 메시지를 전송하고 응답 받기
                    String chatbotResponse = chatService.processMessage(chatMessageDto, payload, session);
                    sendMessageToUser(session, chatbotResponse);
                    break;
                case LEAVE:
                    log.info("User left the room, session id={}, user={}", session.getId(), sender);
                    session.close();
                    break;
            }
        } catch (Exception e) {
            log.error("Error handling message, session id={}, error={}", session.getId(), e.getMessage());
            // 예외 발생 시 연결을 끊지 않고 클라이언트에게 에러 메시지를 보냄
            sendMessageToUser(session, "Error processing message");
        }
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

    private void sendMessageToUser(WebSocketSession session, String message) {
        try {
            //Json 응답 파싱해서 유니코드 이스케이프 시퀀스를 디코딩
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            String decodedResponse = jsonNode.get("response").asText();

            session.sendMessage(new TextMessage(decodedResponse));
        } catch (IOException e) {
            log.error("Failed to send message to user, session id={}, error={}", session.getId(), e.getMessage());
        }
    }
}
