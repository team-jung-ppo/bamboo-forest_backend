package org.jungppo.bambooforest.chatbot.presentation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbots")
public class ChatBotController {

    @GetMapping
    public ResponseEntity<List<ChatBotItemDto>> getChatBots() {
        final List<ChatBotItemDto> chatBotTypeDtos = Stream.of(ChatBotItem.values())
                .map(ChatBotItemDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(chatBotTypeDtos);
    }
}
