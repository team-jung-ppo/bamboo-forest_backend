package org.jungppo.bambooforest.chat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.jungppo.bambooforest.chat.domain.repository.ChatMessageRepository;
import org.jungppo.bambooforest.chat.domain.repository.ChatRoomRepository;
import org.jungppo.bambooforest.chat.dto.ChatBotMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatRoomDto;
import org.jungppo.bambooforest.chat.exception.RoomNotFoundException;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

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

    @Value("${chatbot.api-url}")
    private String chatbotUrl;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository,
                       MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.memberRepository = memberRepository;
        // 5초마다 메시지 배치 저장
        scheduler.scheduleAtFixedRate(this::batchSaveMessages, 0, 5, TimeUnit.SECONDS);
    }

    public String processMessage(ChatMessageDto chatMessageDto, String payload, WebSocketSession session) {
        ChatRoomEntity chatRoom = findChatRoom(chatMessageDto.getRoomId());
        if(chatRoom == null) {
           return "채팅방이 존재하지 않습니다. 다시 생성해 주세요";
        }

        MemberEntity member = findMember(chatMessageDto.getSender());
        if(member == null) {
           return "회원이 존재하지 않습니다. 다시 생성해 주세요";
        }

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
               .chatRoom(chatRoom)
                .member(member)
                .content(chatMessageDto.getContent())
                .build();

        messageBuffer.add(chatMessage);

        // 챗봇에 메시지 전송 및 응답 받기
        String chatbotResponse = sendToChatbot(chatMessageDto);
        if(chatbotResponse == null) {
            return "챗봇 응답이 없습니다. 다시 시도해 주세요";
        }

        return chatbotResponse;
    }

    private ChatRoomEntity findChatRoom(String roomId) {
        try {
            return chatRoomRepository.findByRoomId(roomId)
                    .orElseThrow(RoomNotFoundException::new);
        } catch (RoomNotFoundException e) {
            log.error("Chat room not found: {}", roomId, e);
            return null;
        }
    }

    private MemberEntity findMember(String username) {
        try {
            return memberRepository.findByUsername(username)
                    .orElseThrow(MemberNotFoundException::new);
        } catch (MemberNotFoundException e) {
            log.error("Member not found: {}", username, e);
            return null;
        }
    }

    private String sendToChatbot(ChatMessageDto chatMessageDto) {
        try {
            // 챗봇에 POST 요청을 보내고 응답을 받는 로직 구현
            ChatBotMessageDto chatBotMessageDto = ChatBotMessageDto.builder()
                .message(chatMessageDto.getContent())
                .chatBotType(chatMessageDto.getChatBotType())
                .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(chatbotUrl, chatBotMessageDto,String.class);

            String responseBody = response.getBody();

            return responseBody;
        } catch (Exception e) {
            log.error("Error sending message to chatbot: {}", e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public ChatRoomDto createRoom(String name, Long userId) {
        memberRepository.findById(userId).orElseThrow(MemberNotFoundException::new);

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
            log.debug("No messages to save in this batch");
        }
    }

    @Transactional
    public void saveMessages(List<ChatMessageEntity> messages) {
        chatMessageRepository.saveAll(messages);
        log.info("Saved {} messages to the database", messages.size());
    }
}
