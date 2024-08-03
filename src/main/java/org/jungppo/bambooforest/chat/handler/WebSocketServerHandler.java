package org.jungppo.bambooforest.chat.handler;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.jungppo.bambooforest.chat.domain.repository.ChatRoomRepository;
import org.jungppo.bambooforest.chat.dto.ChatMessageDto;
import org.jungppo.bambooforest.chat.exception.RoomNotFoundException;
import org.jungppo.bambooforest.chat.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            validateSession(session);
            sessions.putIfAbsent(session.getId(), session);
            super.afterConnectionEstablished(session);
        } catch (Exception e) {
            log.error("Connection validation failed: {}", e.getMessage());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        try {
            ChatMessageDto chatMessageDto = parseMessage(payload);
            String roomId = (String) session.getAttributes().get("roomId");
            Long memberId = Long.valueOf((String) session.getAttributes().get("memberId"));

            ChatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId).orElseThrow(RoomNotFoundException::new);
            String chatBotName = chatRoom.getChatBotName().getName();

            handleMessage(session, chatMessageDto, roomId, memberId, chatBotName);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("메시지를 다시 보내주세요"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session.getId());
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        super.afterConnectionClosed(session, status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false; 
    }

    private void validateSession(WebSocketSession session) {
        String roomId = getHeader(session, "roomId");
        String memberId = getHeader(session, "memberId");
        ChatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId).orElseThrow(RoomNotFoundException::new);
        String chatBotName = chatRoom.getChatBotName().getName();

        chatService.validateChatRoomAndMember(roomId, Long.valueOf(memberId), chatBotName);
    }

    private String getHeader(WebSocketSession session, String headerName) {
        return session.getHandshakeHeaders().getFirst(headerName);
    }

    private ChatMessageDto parseMessage(String payload) throws IOException {
        return objectMapper.readValue(payload, ChatMessageDto.class);
    }

    private void handleMessage(WebSocketSession session, ChatMessageDto chatMessageDto, String roomId, Long memberId, String chatBotName) throws Exception {
        switch (chatMessageDto.getType()) {
            case ENTER:
                handleEnterMessage(session, chatMessageDto);
                break;
            case TALK:
                handleTalkMessage(session, chatMessageDto, roomId, memberId, chatBotName);
                break;
            case LEAVE:
                handleLeaveMessage(session);
                break;
            default:
                break;
        }
    }

    private void handleEnterMessage(WebSocketSession session, ChatMessageDto chatMessageDto) {
        // Enter message 처리 로직
        // 필요시 chatService에 추가 로직 구현
    }

    private void handleTalkMessage(WebSocketSession session, ChatMessageDto chatMessageDto, String roomId, Long memberId, String chatBotName) throws Exception {
        String chatbotResponse = chatService.handleMessage(chatMessageDto, roomId, memberId, chatBotName);
        session.sendMessage(new TextMessage(chatbotResponse));
    }

    private void handleLeaveMessage(WebSocketSession session) throws IOException {
        session.close();
    }
}
