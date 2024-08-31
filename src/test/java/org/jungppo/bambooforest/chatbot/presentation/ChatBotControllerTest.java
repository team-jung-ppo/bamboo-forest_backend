package org.jungppo.bambooforest.chatbot.presentation;

import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.AUNT_CHATBOT;
import static org.jungppo.bambooforest.chatbot.domain.ChatBotItem.UNCLE_CHATBOT;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotItemDtoFixture.createChatBotItemDto;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotItemDtoFixture.createChatBotItemDtos;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseDtoFixture.createChatBotPurchaseDto;
import static org.jungppo.bambooforest.chatbot.fixture.ChatBotPurchaseRequestFixture.createChatBotPurchaseRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseDto;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.service.ChatBotPurchaseService;
import org.jungppo.bambooforest.global.config.ObjectMapperConfig;
import org.jungppo.bambooforest.global.exception.service.GlobalExceptionHandler;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ChatBotControllerTest {

    @InjectMocks
    private ChatBotController chatBotController;

    @Mock
    private ChatBotPurchaseService chatBotPurchaseService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatBotController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetChatBots() throws Exception {
        // given
        final List<ChatBotItemDto> chatBotItemDtos = createChatBotItemDtos();

        // when & then
        mockMvc.perform(get("/api/chatbots"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(chatBotItemDtos)));
    }

    @Test
    void testPurchaseChatBot() throws Exception {
        // given
        final ChatBotPurchaseRequest chatBotPurchaseRequest = createChatBotPurchaseRequest(UNCLE_CHATBOT.getName());
        final Long ChatBotPurchaseId = 1L;

        when(chatBotPurchaseService.purchaseChatBot(eq(chatBotPurchaseRequest), any(CustomOAuth2User.class)))
                .thenReturn(ChatBotPurchaseId);

        // when & then
        mockMvc.perform(post("/api/chatbots/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(chatBotPurchaseRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/api/chatbots/purchases/" + ChatBotPurchaseId));
    }

    @Test
    void testGetChatBotPurchase() throws Exception {
        // given
        final ChatBotPurchaseDto chatBotPurchaseDto =
                createChatBotPurchaseDto(1L, UNCLE_CHATBOT.getPrice(), createChatBotItemDto(UNCLE_CHATBOT));

        when(chatBotPurchaseService.getChatBotPurchase(eq(chatBotPurchaseDto.getId()), any(CustomOAuth2User.class)))
                .thenReturn(chatBotPurchaseDto);

        // when & then
        mockMvc.perform(get("/api/chatbots/purchases/{id}", chatBotPurchaseDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(chatBotPurchaseDto)));
    }

    @Test
    void testGetChatBotPurchases() throws Exception {
        // given
        final List<ChatBotPurchaseDto> chatBotPurchases = List.of(
                createChatBotPurchaseDto(1L, UNCLE_CHATBOT.getPrice(), createChatBotItemDto(UNCLE_CHATBOT)),
                createChatBotPurchaseDto(2L, AUNT_CHATBOT.getPrice(), createChatBotItemDto(AUNT_CHATBOT))
        );

        when(chatBotPurchaseService.getChatBotPurchases(any(CustomOAuth2User.class)))
                .thenReturn(chatBotPurchases);

        // when & then
        mockMvc.perform(get("/api/chatbots/purchases"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(chatBotPurchases)));
    }

    private String convertToJson(final Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
