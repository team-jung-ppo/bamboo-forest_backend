package org.jungppo.bambooforest.payment.presentation;

import static org.jungppo.bambooforest.payment.fixture.PaymentConfirmRequestFixture.PAYMENT_CONFIRM_REQUEST;
import static org.jungppo.bambooforest.payment.fixture.PaymentDtoFixture.MEDIUM_BATTERY_PAYMENT_DTO;
import static org.jungppo.bambooforest.payment.fixture.PaymentDtoFixture.SMALL_BATTERY_PAYMENT_DTO;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupRequestFixture.SMALL_BATTERY_SETUP_REQUEST;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupResponseFixture.SMALL_BATTERY_SETUP_RESPONSE;
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
import java.util.List;
import org.jungppo.bambooforest.global.config.ObjectMapperConfig;
import org.jungppo.bambooforest.global.exception.service.GlobalExceptionHandler;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.dto.PaymentDto;
import org.jungppo.bambooforest.payment.service.PaymentService;
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
class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testSetupPayment() throws Exception {
        // given
        when(paymentService.setupPayment(eq(SMALL_BATTERY_SETUP_REQUEST), any(CustomOAuth2User.class)))
                .thenReturn(SMALL_BATTERY_SETUP_RESPONSE);

        // when & then
        mockMvc.perform(post("/api/payments/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(SMALL_BATTERY_SETUP_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(SMALL_BATTERY_SETUP_RESPONSE)));
    }

    @Test
    void testConfirmPayment() throws Exception {
        // given
        when(paymentService.confirmPayment(eq(PAYMENT_CONFIRM_REQUEST), any(CustomOAuth2User.class)))
                .thenReturn(SMALL_BATTERY_PAYMENT_DTO.getId());

        // when & then
        mockMvc.perform(post("/api/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(PAYMENT_CONFIRM_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/api/payments/" + SMALL_BATTERY_PAYMENT_DTO.getId()));
    }

    @Test
    void testGetPayment() throws Exception {
        // given
        when(paymentService.getPayment(eq(SMALL_BATTERY_PAYMENT_DTO.getId()), any(CustomOAuth2User.class)))
                .thenReturn(SMALL_BATTERY_PAYMENT_DTO);

        // when & then
        mockMvc.perform(get("/api/payments/{paymentId}", SMALL_BATTERY_PAYMENT_DTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(SMALL_BATTERY_PAYMENT_DTO)));
    }

    @Test
    void testGetPayments() throws Exception {
        // given
        final List<PaymentDto> paymentDtos = List.of(SMALL_BATTERY_PAYMENT_DTO, MEDIUM_BATTERY_PAYMENT_DTO);

        when(paymentService.getPayments(any(CustomOAuth2User.class)))
                .thenReturn(paymentDtos);

        // when & then
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(paymentDtos)));
    }

    private String convertToJson(final Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}
