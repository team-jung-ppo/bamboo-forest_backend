package org.jungppo.bambooforest.chat.service;

import org.hibernate.sql.exec.ExecutionException;
import org.jungppo.bambooforest.chat.handler.WebSocketServerHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final WebSocketClient webSocketClient;
    private WebSocketSession webSocketSession;
    private final WebSocketServerHandler webSocketServerHandler;

    @Value("${websocket.uri}")
    private String websocketUri;

    @PostConstruct
    public void init() {
        // 초기화시 WebSocket 서버에 연결 시도를 여기서 x
        connectToWebSocketServerAsync(); // TODO : 나중에 init 파일로 따로 빼기
    }

    private void connectToWebSocketServerAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                connectToWebSocketServer();
            } catch (Exception e) {
                log.error("WebSocket 연결 실패", e);
            }
        });
    }

    private void connectToWebSocketServer() throws ExecutionException, InterruptedException {
        URI uri = URI.create("ws://localhost:5000/ws");
        
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

        CompletableFuture<WebSocketSession> future = webSocketClient.execute(webSocketServerHandler, headers, uri);

        future.thenAccept(session -> {
            webSocketSession = session;
            log.info("Successfully connected to WebSocket server");
        }).exceptionally(ex -> {
            log.error("Error connecting to WebSocket server: {}", ex.getMessage());
            return null;
        });
    }

}
