package org.jungppo.bambooforest.chat.presentation;

import org.jungppo.bambooforest.chat.dto.ChatRoomDto;
import org.jungppo.bambooforest.chat.service.ChatService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/createRoom") // 채팅방 생성
    public ResponseEntity<ChatRoomDto> createRoom(@RequestParam String name, @AuthenticationPrincipal CustomOAuth2User oauth2User){
        ChatRoomDto createdRoom = chatService.createRoom(name, oauth2User.getId());
        return ResponseEntity.ok().body(createdRoom);
    }
}
