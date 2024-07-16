package org.jungppo.bambooforest.chatbot.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.service.ChatBotService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chatbots")
public class ChatBotController {

    private final ChatBotService chatBotService;

    @GetMapping
    public ResponseEntity<List<ChatBotItemDto>> getChatBots() {
        final List<ChatBotItemDto> chatBotTypeDtos = Stream.of(ChatBotItem.values())
                .map(ChatBotItemDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(chatBotTypeDtos);
    }

    @PostMapping("/purchase")
    public ResponseEntity<Void> purchaseChatBot(@Valid @RequestBody final ChatBotPurchaseRequest chatBotPurchaseRequest,
                                                @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        chatBotService.purchaseChatBot(chatBotPurchaseRequest, customOAuth2User);
        return ResponseEntity.created(URI.create("/api/members")).build();
    }
}
