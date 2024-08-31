package org.jungppo.bambooforest.chatbot.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.service.ChatBotPurchaseService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/chatbots")
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotPurchaseService chatBotPurchaseService;

    @GetMapping
    public ResponseEntity<List<ChatBotItemDto>> getChatBots() {
        final List<ChatBotItemDto> chatBotItemDtos = Stream.of(ChatBotItem.values())
                .map(ChatBotItemDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(chatBotItemDtos);
    }

    @PostMapping("/purchase")
    public ResponseEntity<Void> purchaseChatBot(@Valid @RequestBody final ChatBotPurchaseRequest chatBotPurchaseRequest,
                                                @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        Long ChatBotPurchaseEntityId = chatBotPurchaseService.purchaseChatBot(chatBotPurchaseRequest, customOAuth2User);
        return ResponseEntity.created(URI.create("/api/chatbots/purchases/" + ChatBotPurchaseEntityId)).build();
    }

    @GetMapping("/purchases/{chatBotPurchaseId}")
    public ResponseEntity<ChatBotPurchaseDto> getChatBotPurchase(
            @PathVariable(name = "chatBotPurchaseId") final Long chatBotPurchaseId,
            @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        final ChatBotPurchaseDto chatBotPurchaseDto = chatBotPurchaseService.getChatBotPurchase(chatBotPurchaseId,
                customOAuth2User);
        return ResponseEntity.ok().body(chatBotPurchaseDto);
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<ChatBotPurchaseDto>> getChatBotPurchases(
            @AuthenticationPrincipal final CustomOAuth2User customOAuth2User) {
        final List<ChatBotPurchaseDto> chatBotPurchases = chatBotPurchaseService.getChatBotPurchases(customOAuth2User);
        return ResponseEntity.ok().body(chatBotPurchases);
    }
}
