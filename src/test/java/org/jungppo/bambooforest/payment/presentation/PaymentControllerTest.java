package org.jungppo.bambooforest.payment.presentation;

import static org.jungppo.bambooforest.battery.domain.BatteryItem.SMALL_BATTERY;
import static org.jungppo.bambooforest.battery.fixture.BatteryItemDtoFixture.createBatteryItemDto;
import static org.jungppo.bambooforest.payment.fixture.PaymentConfirmRequestFixture.createPaymentConfirmRequest;
import static org.jungppo.bambooforest.payment.fixture.PaymentDtoFixture.createPaymentDto;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupRequestFixture.createPaymentSetupRequest;
import static org.jungppo.bambooforest.payment.fixture.PaymentSetupResponseFixture.createPaymentSetupResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.battery.dto.BatteryItemDto;
import org.jungppo.bambooforest.global.config.ObjectMapperConfig;
import org.jungppo.bambooforest.global.exception.service.GlobalExceptionHandler;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.dto.PaymentConfirmRequest;
import org.jungppo.bambooforest.member.dto.PaymentDto;
import org.jungppo.bambooforest.member.dto.PaymentSetupRequest;
import org.jungppo.bambooforest.member.dto.PaymentSetupResponse;
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
        final PaymentSetupRequest setupRequest = createPaymentSetupRequest(SMALL_BATTERY.getName());
        final PaymentSetupResponse setupResponse =
                createPaymentSetupResponse(UUID.randomUUID(), SMALL_BATTERY.getPrice());

        given(paymentService.setupPayment(eq(setupRequest), any(CustomOAuth2User.class)))
                .willReturn(setupResponse);

        // when & then
        mockMvc.perform(post("/api/payments/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(setupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(setupResponse)));
    }

    @Test
    void testConfirmPayment() throws Exception {
        // given
        final PaymentConfirmRequest confirmRequest =
                createPaymentConfirmRequest("validPaymentKey", UUID.randomUUID(), SMALL_BATTERY.getPrice());

        given(paymentService.confirmPayment(eq(confirmRequest), any(CustomOAuth2User.class)))
                .willReturn(confirmRequest.getOrderId());

        // when & then
        mockMvc.perform(post("/api/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(confirmRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/api/payments/" + confirmRequest.getOrderId()));
    }

    @Test
    void testGetPayment() throws Exception {
        // given
        final BatteryItemDto batteryItemDto = createBatteryItemDto(SMALL_BATTERY);
        final PaymentDto paymentDto = createPaymentDto(UUID.randomUUID(), batteryItemDto,
                batteryItemDto.getPrice());

        given(paymentService.getPayment(eq(paymentDto.getId()), any(CustomOAuth2User.class)))
                .willReturn(paymentDto);

        // when & then
        mockMvc.perform(get("/api/payments/{paymentId}", paymentDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(paymentDto)));
    }

    @Test
    void testGetPayments() throws Exception {
        // given
        final PaymentDto paymentDto1 = createPaymentDto(UUID.randomUUID(),
                createBatteryItemDto(SMALL_BATTERY), SMALL_BATTERY.getPrice());
        final PaymentDto paymentDto2 = createPaymentDto(UUID.randomUUID(),
                createBatteryItemDto(BatteryItem.MEDIUM_BATTERY), SMALL_BATTERY.getPrice());

        final List<PaymentDto> paymentDtos = List.of(paymentDto1, paymentDto2);

        given(paymentService.getPayments(any(CustomOAuth2User.class)))
                .willReturn(paymentDtos);

        // when & then
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(paymentDtos)));
    }

    private String convertToJson(final Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}
