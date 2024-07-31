package org.jungppo.bambooforest.chat.presentation;

import org.jungppo.bambooforest.chat.dto.ChatListDto;
import org.jungppo.bambooforest.chat.dto.ChatRoomDto;
import org.jungppo.bambooforest.chat.service.ChatService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/createRoom") // 채팅방 생성
    public ResponseEntity<ChatRoomDto> createRoom(@RequestParam String name, @AuthenticationPrincipal CustomOAuth2User oauth2User){
        ChatRoomDto createdRoom = chatService.createRoom(name, oauth2User.getId());
        return ResponseEntity.ok().body(createdRoom);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Page<ChatListDto>> getChatList(
        @PathVariable String roomId, 
        @AuthenticationPrincipal CustomOAuth2User oauth2User,
        Pageable pageable){
        Page<ChatListDto> chatList = chatService.getChatList(roomId, oauth2User.getId(), pageable);
        return ResponseEntity.ok().body(chatList);
    }
}
