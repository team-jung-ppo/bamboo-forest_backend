package org.jungppo.bambooforest.chat.service;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.jungppo.bambooforest.chat.domain.repository.ChatMessageRepository;
import org.jungppo.bambooforest.chat.domain.repository.ChatRoomRepository;
import org.jungppo.bambooforest.chat.dto.ChatBotMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatRoomDto;
import org.jungppo.bambooforest.chat.exception.RoomNotFoundException;
import org.jungppo.bambooforest.global.exception.domain.ExceptionType;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<ChatMessageEntity> messageBuffer = Collections.synchronizedList(new ArrayList<>());

    private final Map<String, ChatRoomDto> chatRooms = new ConcurrentHashMap<>();

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.memberRepository = memberRepository;
        // 5초마다 메시지 배치 저장
        scheduler.scheduleAtFixedRate(this::batchSaveMessages, 0, 5, TimeUnit.SECONDS);
    }

    public String processMessage(ChatMessageDto chatMessageDto, String payload, WebSocketSession session) {
        ChatRoomEntity chatRoom = chatRoomRepository.findByRoomId(chatMessageDto.getRoomId()).orElseThrow(RoomNotFoundException::new);
        MemberEntity member = memberRepository.findByUsername(chatMessageDto.getSender()).orElseThrow(MemberNotFoundException::new);
        
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
            .chatRoom(chatRoom)
            .member(member)
            .content(chatMessageDto.getContent())
            .build();

        messageBuffer.add(chatMessage);
        
        // 챗봇에 메시지 전송 및 응답 받기
        String chatbotResponse = sendToChatbot(chatMessageDto);

        return chatbotResponse;
    }

    private String sendToChatbot(ChatMessageDto chatMessageDto) {
        // 챗봇에 POST 요청을 보내고 응답을 받는 로직 구현
        ChatBotMessageDto chatBotMessageDto = ChatBotMessageDto.builder()
            .content(chatMessageDto.getContent())
            .chatBotType(chatMessageDto.getChatBotType())
            .build();
        
        RestTemplate restTemplate = new RestTemplate();
        String chatbotUrl = "http://chatbot.api/endpoint"; // 챗봇 API URL 수정 필요
        ResponseEntity<String> response = restTemplate.postForEntity(chatbotUrl, chatBotMessageDto, String.class); //response entity 수정필요
        return response.getBody();
    }

    @Transactional
    public ChatRoomDto createRoom(String name, String username) {
        String randomId = UUID.randomUUID().toString();
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .roomId(randomId)
                .name(name)
                .build();
        chatRooms.put(randomId, convertToDTO(chatRoomEntity));
        chatRoomRepository.save(chatRoomEntity);
        return convertToDTO(chatRoomEntity);
    }

    private ChatRoomDto convertToDTO(ChatRoomEntity chatRoomEntity) {
        return ChatRoomDto.builder()
                .roomId(chatRoomEntity.getRoomId())
                .name(chatRoomEntity.getName())
                .build();
    }

    // 메시지를 배치로 저장하는 메서드
    private void batchSaveMessages() {
        if (!messageBuffer.isEmpty()) {
            List<ChatMessageEntity> messagesToSave;
            synchronized (messageBuffer) {
                messagesToSave = new ArrayList<>(messageBuffer);
                messageBuffer.clear();
            }
            log.info("Batch saving {} messages", messagesToSave.size());
            saveMessages(messagesToSave);
        } else {
            log.info("No messages to save in this batch");
        }
    }

    @Transactional
    public void saveMessages(List<ChatMessageEntity> messages) {
        chatMessageRepository.saveAll(messages);
        log.info("Saved {} messages to the database", messages.size());
    }
}
