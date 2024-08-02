package org.jungppo.bambooforest.chat.presentation;

import org.jungppo.bambooforest.chat.dto.ChatMessageListDto;
import org.jungppo.bambooforest.chat.dto.ChatRoomDto;
import org.jungppo.bambooforest.chat.dto.CreateRoomRequest;
import org.jungppo.bambooforest.chat.service.ChatService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room") // 채팅방 생성
    public ResponseEntity<ChatRoomDto> createRoom(
        @RequestBody @Valid CreateRoomRequest createRoomRequest,
        @AuthenticationPrincipal CustomOAuth2User oauth2User){
        ChatRoomDto createdRoom = chatService.createChatRoom(oauth2User.getId(), createRoomRequest.getChatBotType());
        return ResponseEntity.ok().body(createdRoom);
    }

    @GetMapping("/rooms/{roomId}") // 채팅방 메시지 조회
    public ResponseEntity<Page<ChatMessageListDto>> getChatList(
        @PathVariable String roomId, 
        @AuthenticationPrincipal CustomOAuth2User oauth2User,
        Pageable pageable){
        Page<ChatMessageListDto> chatList = chatService.fetchChatMessages(roomId, oauth2User.getId(), pageable);
        return ResponseEntity.ok().body(chatList);
    }

    @GetMapping // 채팅방 리스트 조회
    public ResponseEntity<Page<ChatRoomDto>> getChatRoomList(
        @AuthenticationPrincipal CustomOAuth2User oauth2User,
        Pageable pageable){
        Page<ChatRoomDto> chatRoomList = chatService.fetchChatRooms(oauth2User.getId(), pageable);
        return ResponseEntity.ok().body(chatRoomList);
    }

    @DeleteMapping("/rooms/{roomId}") // 채팅방 삭제
    public ResponseEntity<Void> deleteRoom(
        @PathVariable String roomId,
        @AuthenticationPrincipal CustomOAuth2User oauth2User) {
        chatService.removeChatRoom(roomId, oauth2User.getId());
        return ResponseEntity.noContent().build();
    }
}
