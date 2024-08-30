package org.jungppo.bambooforest.battery.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.battery.dto.BatteryItemDto;
import org.jungppo.bambooforest.global.config.ObjectMapperConfig;
import org.jungppo.bambooforest.global.exception.service.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BatteryControllerTest {

    @InjectMocks
    private BatteryController batteryController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(batteryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetBatteryItems() throws Exception {
        // given
        final List<BatteryItemDto> batteryItemDtos = Stream.of(BatteryItem.values())
                .map(BatteryItemDto::from)
                .collect(Collectors.toList());

        // when & then
        mockMvc.perform(get("/api/batteries"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(batteryItemDtos)));
    }

    private String convertToJson(final Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
