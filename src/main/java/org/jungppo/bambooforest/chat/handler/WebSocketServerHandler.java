package org.jungppo.bambooforest.chat.handler;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.jungppo.bambooforest.chat.domain.repository.ChatRoomRepository;
import org.jungppo.bambooforest.chat.dto.ChatMessageDto;
import org.jungppo.bambooforest.chat.exception.RoomNotFoundException;
import org.jungppo.bambooforest.chat.service.ChatService;
import org.jungppo.bambooforest.global.jwt.exception.AuthenticationEntryPointException;
import org.jungppo.bambooforest.global.jwt.exception.TokenExpiredException;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WebSocketServerHandler extends TextWebSocketHandler { // 웹소켓 연결 시간 및 연결 조건 다시 리펙토링
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, ScheduledExecutorService> sessionSchedulers = new ConcurrentHashMap<>();
    private final Map<String, Long> lastMessageTimestamps = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtService jwtService;

    private static final long MESSAGE_TIMEOUT = 10;

    public WebSocketServerHandler(@Qualifier(JWT_ACCESS_TOKEN_SERVICE) JwtService jwtService, ChatService chatService, ObjectMapper objectMapper, ChatRoomRepository chatRoomRepository) {
        this.jwtService = jwtService;
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established: {}", session.getId());
        try {
            sessions.putIfAbsent(session.getId(), session);
            lastMessageTimestamps.put(session.getId(), System.currentTimeMillis());
            startMessageTimeoutTask(session);
            super.afterConnectionEstablished(session);
        } catch (Exception e) {
            log.error("Connection validation failed: {}", e.getMessage());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received message: {} from session: {}", message.getPayload(), session.getId());
        String payload = message.getPayload();
        ChatMessageDto chatMessageDto = parseMessage(payload);

        try {
            handleMessage(session, chatMessageDto);
            lastMessageTimestamps.put(session.getId(), System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error handling message: {}", e.getMessage());
            session.sendMessage(new TextMessage("메시지를 다시 보내주세요"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error in session: {}: {}", session.getId(), exception.getMessage());
        sessions.remove(session.getId());
        stopMessageTimeoutTask(session);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection closed: {} with status: {}", session.getId(), status);
        sessions.remove(session.getId());
        stopMessageTimeoutTask(session);
        super.afterConnectionClosed(session, status);
    }


    private ChatMessageDto parseMessage(String payload) throws IOException {
        return objectMapper.readValue(payload, ChatMessageDto.class);
    }

    private void handleMessage(WebSocketSession session, ChatMessageDto chatMessageDto) throws Exception {
        switch (chatMessageDto.getType()) {
            case AUTH:
                handleAuthMessage(session, chatMessageDto);
                break;
            case ENTER:
                handleEnterMessage(session, chatMessageDto);
                break;
            case TALK:
                handleTalkMessage(session, chatMessageDto);
                break;
            case LEAVE:
                handleLeaveMessage(session);
                break;
            default:
                break;
        }
    }

    private void handleAuthMessage(WebSocketSession session, ChatMessageDto chatMessageDto) throws Exception {
        validateSession(session, chatMessageDto);
    }

    private void validateSession(WebSocketSession session, ChatMessageDto chatMessageDto) throws Exception {
        log.info("Handling AUTH message for session: {}", session.getId());
        String token = extractBearerToken(chatMessageDto.getToken());
        if(!validateToken(token)) {
            session.close(CloseStatus.BAD_DATA);
        }

        String roomId = chatMessageDto.getRoomId();
        Long memberId = chatMessageDto.getMemberId();
        ChatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId).orElseThrow(RoomNotFoundException::new);
        String chatBotName = chatRoom.getChatBotItem().getName();

        session.getAttributes().put("roomId", roomId);
        session.getAttributes().put("memberId", memberId.toString());
        session.getAttributes().put("chatBotName", chatBotName);

        chatService.validateChatRoomAndMember(roomId, memberId, chatBotName);
    }

    private String extractBearerToken(String token) {
        return Optional.ofNullable(token)
                       .filter(t -> t.startsWith("Bearer "))
                       .map(t -> t.substring(7))
                       .orElseThrow(AuthenticationEntryPointException::new);
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

    private void handleEnterMessage(WebSocketSession session, ChatMessageDto chatMessageDto) {
        // Enter message 처리 로직
        // 필요시 chatService에 추가 로직 구현
    }

    private void handleTalkMessage(WebSocketSession session, ChatMessageDto chatMessageDto) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        Long memberId = Long.valueOf((String) session.getAttributes().get("memberId"));
        String chatBotName = (String) session.getAttributes().get("chatBotName");
        String chatbotResponse = chatService.handleMessage(chatMessageDto, roomId, memberId, chatBotName);
        session.sendMessage(new TextMessage(chatbotResponse));
    }

    private void handleLeaveMessage(WebSocketSession session) throws IOException {
        session.close();
    }

    private void startMessageTimeoutTask(WebSocketSession session) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    long lastMessageTime = lastMessageTimestamps.get(session.getId());
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastMessageTime > MESSAGE_TIMEOUT * 60 * 1000) {
                        log.warn("No message received from session: {}, closing connection", session.getId());
                        session.close(CloseStatus.SESSION_NOT_RELIABLE);
                    }
                }
            } catch (IOException e) {
                log.error("Error closing session due to timeout: {}", e.getMessage());
            }
        }, MESSAGE_TIMEOUT, MESSAGE_TIMEOUT, TimeUnit.MINUTES);
        sessionSchedulers.put(session.getId(), scheduler);
    }
    private void stopMessageTimeoutTask(WebSocketSession session) {
        ScheduledExecutorService scheduler = sessionSchedulers.remove(session.getId());
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        lastMessageTimestamps.remove(session.getId());
    }
}
