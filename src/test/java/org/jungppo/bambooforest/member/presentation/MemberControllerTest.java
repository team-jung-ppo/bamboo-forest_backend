package org.jungppo.bambooforest.member.presentation;

import static org.jungppo.bambooforest.global.jwt.fixture.JwtDtoFixture.createJwtDto;
import static org.jungppo.bambooforest.member.fixture.MemberDtoFixture.createMemberDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.jungppo.bambooforest.global.config.ObjectMapperConfig;
import org.jungppo.bambooforest.global.exception.service.GlobalExceptionHandler;
import org.jungppo.bambooforest.global.jwt.dto.JwtDto;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.dto.MemberDto;
import org.jungppo.bambooforest.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberService memberService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testLogout() throws Exception {
        // given & when & then
        mockMvc.perform(post("/api/members/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMember() throws Exception {
        // given
        MemberDto memberDto =
                createMemberDto(1L, null, "username", "profileImageUrl", null, 0, null);
        when(memberService.getMember(any(CustomOAuth2User.class))).thenReturn(memberDto);

        // when & then
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(memberDto)));
    }

    @Test
    void testReissuanceToken() throws Exception {
        // given
        JwtDto jwtDto = createJwtDto("accessToken", "refreshToken");
        when(memberService.reissuanceToken(eq(jwtDto.getRefreshToken()))).thenReturn(jwtDto);

        // when & then
        mockMvc.perform(post("/api/members/reissuance")
                        .header(AUTHORIZATION, jwtDto.getRefreshToken()))
                .andExpect(status().isCreated())
                .andExpect(content().json(convertToJson(jwtDto)));
    }

    @Test
    void testDeleteMember() throws Exception {
        // given & when & then
        mockMvc.perform(delete("/api/members"))
                .andExpect(status().isNoContent());
    }

    private String convertToJson(final Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
