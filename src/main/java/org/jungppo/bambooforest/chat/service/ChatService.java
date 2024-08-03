package org.jungppo.bambooforest.chat.service;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.jungppo.bambooforest.chat.domain.repository.ChatMessageRepository;
import org.jungppo.bambooforest.chat.domain.repository.ChatRoomRepository;
import org.jungppo.bambooforest.chat.dto.ChatBotMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatMessageListDto;
import org.jungppo.bambooforest.chat.dto.ChatMessageDto;
import org.jungppo.bambooforest.chat.dto.ChatRoomDto;
import org.jungppo.bambooforest.chat.exception.RoomNotFoundException;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.exception.ChatBotPurchaseNotFoundException;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.chatbot.exception.ChatBotTypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        scheduler.scheduleAtFixedRate(this::batchSaveMessages, 0, 30, TimeUnit.SECONDS);
    }

    public String handleMessage(ChatMessageDto chatMessageDto, String roomId, Long memberId) {
        try {
            ChatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId)
                        .orElseThrow(RoomNotFoundException::new);
            MemberEntity member = memberRepository.findById(memberId)
                        .orElseThrow(MemberNotFoundException::new);
    
            String chatbotResponse = requestChatbotResponse(chatMessageDto);
            if (chatbotResponse == null) {
                return "챗봇 응답이 없습니다. 다시 시도해 주세요";
            }
    
            String decodedResponse = decodeChatbotResponse(chatbotResponse);
            if (decodedResponse == null) {
                return "응답 디코딩 중 오류가 발생했습니다. 다시 시도해 주세요";
            }
    
            storeMessage(chatRoom, member, chatMessageDto, decodedResponse);
    
            return decodedResponse;
        } catch (Exception e) {
            return "내부 서버 오류가 발생했습니다. 나중에 다시 시도해 주세요.";
        }
    }

    public void validateChatRoomAndMember(String roomId, Long memberId, String chatBotName) {
        chatRoomRepository.findByRoomId(roomId).orElseThrow(RoomNotFoundException::new);
    
        MemberEntity member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    
        if (!member.hasPurchasedChatBot(chatBotName)) {
            throw new ChatBotPurchaseNotFoundException();
        }
        
        // 구매한 챗봇의 타입이 요청된 타입과 일치하는지 검증
        ChatBotItem.findByName(chatBotName).orElseThrow(ChatBotTypeMismatchException::new);
    }

    private String requestChatbotResponse(ChatMessageDto chatMessageDto) {
        return sendToChatbot(chatMessageDto);
    }
    
    private String decodeChatbotResponse(String chatbotResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(chatbotResponse);
            return jsonNode.get("response").asText();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private void storeMessage(ChatRoomEntity chatRoom, MemberEntity member, ChatMessageDto chatMessageDto, String decodedResponse) {
        ChatMessageEntity userMessage = ChatMessageEntity.of(chatRoom, member, chatMessageDto.getMessage(), decodedResponse, chatMessageDto.getChatBotName());
        messageBuffer.add(userMessage);
    }

    // 메시지를 배치로 저장하는 메서드
    public void batchSaveMessages() {
        if (!messageBuffer.isEmpty()) {
            List<ChatMessageEntity> messagesToSave;
            synchronized (messageBuffer) {
                messagesToSave = new ArrayList<>(messageBuffer);
                messageBuffer.clear();
            }
            saveMessages(messagesToSave);
        }
    }

    @Transactional
    public void saveMessages(List<ChatMessageEntity> messages) {
        chatMessageRepository.saveAll(messages);
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
            return null;
        }
    }

    // 채팅방 생성
    @Transactional
    public ChatRoomDto createChatRoom(Long userId, String chatBotName) { 
        MemberEntity member = memberRepository.findById(userId).orElseThrow(MemberNotFoundException::new);
        // 사용자가 챗봇을 구매했는지 검증
        if (!member.hasPurchasedChatBot(chatBotName)) {
            throw new ChatBotPurchaseNotFoundException();
        }
        
        // 구매한 챗봇의 타입이 요청된 타입과 일치하는지 검증
        ChatBotItem chatBotItem = ChatBotItem.findByName(chatBotName).orElseThrow(ChatBotTypeMismatchException::new);
        
        String randomId = UUID.randomUUID().toString();
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.of(randomId, member, chatBotItem);
        chatRoomRepository.save(chatRoomEntity);
        return ChatRoomDto.from(chatRoomEntity);
    }

    // 채팅 기록 불러오기
    public Page<ChatMessageListDto> fetchChatMessages(String roomId, Long userId, Pageable pageable) {
        memberRepository.findById(userId).orElseThrow(MemberNotFoundException::new);
        ChatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId).orElseThrow(RoomNotFoundException::new);
        Page<ChatMessageEntity> pagedMessages = chatMessageRepository.findPagedMessagesByMemberId(chatRoom.getId(), userId, pageable);
        return pagedMessages.map(ChatMessageListDto::from);
    }

    // 채팅방 리스트 조회
    public Page<ChatRoomDto> fetchChatRooms(Long userId, Pageable pageable) {
        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(MemberNotFoundException::new);
        Page<ChatRoomEntity> chatRooms = chatRoomRepository.findChatRoomsByMemberId(member.getId(), pageable);
        return chatRooms.map(ChatRoomDto::from);
    }

    @Transactional
    public void removeChatRoom(String roomId, Long userId) {
        chatRoomRepository.findByRoomId(roomId).ifPresent(chatRoom -> {
            if(!chatRoom.getMember().getId().equals(userId)) throw new MemberNotFoundException();
            chatMessageRepository.deleteAllByChatRoomId(chatRoom.getId());
            chatRoomRepository.delete(chatRoom);
        });
    }
}
