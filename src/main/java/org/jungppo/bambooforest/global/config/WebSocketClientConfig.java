package org.jungppo.bambooforest.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration // 챗봇과 백엔드 websocket 연결 설정
public class WebSocketClientConfig {
    @Bean
    public StandardWebSocketClient standardWebSocketClient() {
        return new StandardWebSocketClient();
    }
}
