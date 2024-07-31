package org.jungppo.bambooforest.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.jungppo.bambooforest.chat.domain.repository.ChatMessageRepository;
import org.jungppo.bambooforest.chat.domain.repository.ChatRoomRepository;
import org.jungppo.bambooforest.chat.dto.ChatBotMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatListDto;
import org.jungppo.bambooforest.chat.dto.ChatMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatRoomDto;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
    private final ObjectMapper objectMapper;

    @Value("${chatbot.api-url}")
    private String chatbotUrl;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository,
                       MemberRepository memberRepository, ObjectMapper objectMapper) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
        // 5초마다 메시지 배치 저장
        scheduler.scheduleAtFixedRate(this::batchSaveMessages, 0, 10, TimeUnit.SECONDS);
    }

    public String processMessage(ChatMessageDto chatMessageDto, String payload, WebSocketSession session) {
        try {
            ChatRoomEntity chatRoom = validateChatRoom(chatMessageDto.getRoomId());
            if (chatRoom == null) {
                return "채팅방이 존재하지 않습니다. 다시 생성해 주세요";
            }
    
            MemberEntity member = validateMember(chatMessageDto.getMemberId());
            if (member == null) {
                return "회원이 존재하지 않습니다. 다시 생성해 주세요";
            }
    
            String chatbotResponse = getChatbotResponse(chatMessageDto);
            if (chatbotResponse == null) {
                return "챗봇 응답이 없습니다. 다시 시도해 주세요";
            }
    
            String decodedResponse = decodeResponse(chatbotResponse);
            if (decodedResponse == null) {
                return "응답 디코딩 중 오류가 발생했습니다. 다시 시도해 주세요";
            }
    
            saveMessage(chatRoom, member, chatMessageDto, decodedResponse);
    
            return decodedResponse;
        } catch (Exception e) {
            log.error("메시지 처리 중 예상치 못한 오류 발생: {}", e.getMessage());
            return "내부 서버 오류가 발생했습니다. 나중에 다시 시도해 주세요.";
        }
    }

    private ChatRoomEntity validateChatRoom(String roomId) {
        ChatRoomEntity chatRoom = findChatRoom(roomId);
        if (chatRoom == null) {
            log.warn("채팅방을 찾을 수 없음: roomId={}", roomId);
        }
        return chatRoom;
    }
    
    private MemberEntity validateMember(Long memberId) {
        MemberEntity member = findMember(memberId);
        if (member == null) {
            log.warn("회원을 찾을 수 없음: memberId={}", memberId);
        }
        return member;
    }

    private ChatRoomEntity findChatRoom(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).orElse(null);
    }

    private MemberEntity findMember(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }
    
    private String getChatbotResponse(ChatMessageDto chatMessageDto) {
        String chatbotResponse = sendToChatbot(chatMessageDto);
        if (chatbotResponse == null) {
            log.warn("챗봇 응답 없음: chatMessageDto={}", chatMessageDto);
        }
        return chatbotResponse;
    }
    
    private String decodeResponse(String chatbotResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(chatbotResponse);
            return jsonNode.get("response").asText();
        } catch (JsonProcessingException e) {
            log.error("응답 디코딩 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    private void saveMessage(ChatRoomEntity chatRoom, MemberEntity member, ChatMessageDto chatMessageDto, String decodedResponse) {
        ChatMessageEntity userMessage = ChatMessageEntity.createMessage(chatRoom, member, chatMessageDto.getMessage(), decodedResponse, chatMessageDto.getChatBotType());
        messageBuffer.add(userMessage);
        log.info("메시지 저장됨: chatRoom={}, member={}, message={}", chatRoom.getRoomId(), member.getId(), chatMessageDto.getMessage());
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

    //챗봇 응답 요청
    private String sendToChatbot(ChatMessageDto chatMessageDto) {
        try {
            // 챗봇에 POST 요청을 보내고 응답을 받는 로직 구현
            ChatBotMessageDto chatBotMessageDto = ChatBotMessageDto.from(chatMessageDto);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(chatbotUrl, chatBotMessageDto,String.class);

            String responseBody = response.getBody();

            return responseBody;
        } catch (Exception e) {
            log.error("Error sending message to chatbot: {}", e.getMessage(), e);
            return null;
        }
    }

    // 채팅방 생성
    @Transactional
    public ChatRoomDto createRoom(String name, Long userId) { 
        memberRepository.findById(userId).orElseThrow(MemberNotFoundException::new);
        String randomId = UUID.randomUUID().toString();
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.create(randomId, name);
        chatRoomRepository.save(chatRoomEntity);
        return convertToDTO(chatRoomEntity);
    }

    private ChatRoomDto convertToDTO(ChatRoomEntity chatRoomEntity) {
        return ChatRoomDto.create(chatRoomEntity.getRoomId(), chatRoomEntity.getName());
    }

    // 채팅 기록 불러오기
    public List<ChatListDto> getChatList(String roomId, Long userId, int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        List<ChatMessageEntity> lastMessages = chatMessageRepository.findLastMessagesByMemberId(roomId, userId, pageable);
        return lastMessages.stream()
            .map(this::convertToChatListDto)
            .collect(Collectors.toList());
    }

    private ChatListDto convertToChatListDto(ChatMessageEntity lastMessage) {
        return ChatListDto.of(lastMessage);
    }
}
