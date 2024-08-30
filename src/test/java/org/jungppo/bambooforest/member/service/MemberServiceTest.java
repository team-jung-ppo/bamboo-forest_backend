package org.jungppo.bambooforest.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jungppo.bambooforest.global.jwt.fixture.JwtDtoFixture.JWT_DTO;
import static org.jungppo.bambooforest.global.jwt.fixture.JwtMemberClaimFixture.JWT_MEMBER_CLAIM;
import static org.jungppo.bambooforest.global.oauth2.fixture.CustomOAuth2UserFixture.CUSTOM_OAUTH2_USER;
import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.MEMBER_ENTITY;
import static org.jungppo.bambooforest.member.fixture.RefreshTokenEntityFixture.REFRESH_TOKEN_ENTITY;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.oauth2.OAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.github.GitHubOAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.kakao.KakaoOAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.kakao.dto.KakaoUnlinkSuccessResponse;
import org.jungppo.bambooforest.global.jwt.dto.JwtDto;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.member.domain.MemberDeleteEvent;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.MemberDto;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.member.exception.RefreshTokenFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private OAuth2AuthorizedClientService jdbcOAuth2AuthorizedClientServiceProxy;

    @Mock
    private JwtService jwtAccessTokenService;

    @Mock
    private JwtService jwtRefreshTokenService;

    @Spy
    private List<OAuth2Client> oAuth2Clients = new ArrayList<>();

    @Mock
    private KakaoOAuth2Client kakaoOAuth2Client;

    @Mock
    private GitHubOAuth2Client gitHubOAuth2Client;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setup() {
        memberService = new MemberService(
                memberRepository,
                jdbcOAuth2AuthorizedClientServiceProxy,
                refreshTokenService,
                jwtAccessTokenService,
                jwtRefreshTokenService,
                oAuth2Clients,
                applicationEventPublisher
        );
        oAuth2Clients.add(kakaoOAuth2Client);
        oAuth2Clients.add(gitHubOAuth2Client);
    }

    @Test
    void testLogout() {
        // given & when
        memberService.logout(CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            verify(refreshTokenService).deleteById(eq(CUSTOM_OAUTH2_USER.getId()));
            verify(jdbcOAuth2AuthorizedClientServiceProxy).removeAuthorizedClient(
                    eq(CUSTOM_OAUTH2_USER.getOAuth2Type().getRegistrationId()),
                    eq(CUSTOM_OAUTH2_USER.getId().toString()));
        });
    }

    @Test
    void testGetMember() {
        // given
        when(memberRepository.findById(eq(CUSTOM_OAUTH2_USER.getId()))).thenReturn(Optional.of(MEMBER_ENTITY));

        // when
        final MemberDto memberDto = memberService.getMember(CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            softly.assertThat(memberDto.getId()).isEqualTo(MEMBER_ENTITY.getId());
            softly.assertThat(memberDto.getUsername()).isEqualTo(MEMBER_ENTITY.getUsername());
            verify(memberRepository).findById(eq(CUSTOM_OAUTH2_USER.getId()));
        });
    }

    @Test
    void testGetMember_MemberNotFound() {
        // given
        when(memberRepository.findById(eq(CUSTOM_OAUTH2_USER.getId()))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMember(CUSTOM_OAUTH2_USER))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void testReissuanceToken() {
        // given
        when(jwtRefreshTokenService.parseOptionalToken(eq(JWT_DTO.getRefreshToken()))).thenReturn(
                Optional.of(JWT_MEMBER_CLAIM));
        when(refreshTokenService.findById(eq(CUSTOM_OAUTH2_USER.getId()))).thenReturn(
                Optional.of(REFRESH_TOKEN_ENTITY));
        when(jwtAccessTokenService.createToken(eq(JWT_MEMBER_CLAIM))).thenReturn(JWT_DTO.getAccessToken());
        when(jwtRefreshTokenService.createToken(eq(JWT_MEMBER_CLAIM))).thenReturn(JWT_DTO.getRefreshToken());

        // when
        final JwtDto jwtDto = memberService.reissuanceToken(JWT_DTO.getRefreshToken());

        // then
        assertSoftly(softly -> {
            softly.assertThat(jwtDto.getAccessToken()).isEqualTo(JWT_DTO.getAccessToken());
            softly.assertThat(jwtDto.getRefreshToken()).isEqualTo(JWT_DTO.getRefreshToken());
            verify(jwtRefreshTokenService).parseOptionalToken(eq(JWT_DTO.getRefreshToken()));
            verify(refreshTokenService).saveOrUpdateRefreshToken(eq(CUSTOM_OAUTH2_USER.getId()),
                    eq(JWT_DTO.getRefreshToken()));
            verify(jwtAccessTokenService).createToken(eq(JWT_MEMBER_CLAIM));
            verify(jwtRefreshTokenService).createToken(eq(JWT_MEMBER_CLAIM));
        });
    }

    @Test
    void testReissuanceToken_RefreshTokenInvalid() {
        // given
        when(jwtRefreshTokenService.parseOptionalToken(eq(JWT_DTO.getRefreshToken()))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.reissuanceToken(JWT_DTO.getRefreshToken()))
                .isInstanceOf(RefreshTokenFailureException.class);
    }

    @Test
    void testDeleteMember() {
        // given
        final String oauth2MemberId = MEMBER_ENTITY.getName().split("_", 3)[2];

        when(memberRepository.findById(eq(CUSTOM_OAUTH2_USER.getId()))).thenReturn(Optional.of(MEMBER_ENTITY));
        when(kakaoOAuth2Client.supports(OAUTH2_KAKAO)).thenReturn(true);
        when(kakaoOAuth2Client.unlink(eq(oauth2MemberId))).thenReturn(
                ClientResponse.success(new KakaoUnlinkSuccessResponse("Successfully unlinked Kakao account.")));

        // when
        memberService.deleteMember(CUSTOM_OAUTH2_USER);

        // then
        assertSoftly(softly -> {
            verify(memberRepository).findById(eq(CUSTOM_OAUTH2_USER.getId()));
            verify(jdbcOAuth2AuthorizedClientServiceProxy).removeAuthorizedClient(
                    eq(CUSTOM_OAUTH2_USER.getOAuth2Type().getRegistrationId()),
                    eq(CUSTOM_OAUTH2_USER.getId().toString()));
            verify(kakaoOAuth2Client).unlink(eq(oauth2MemberId));
            verify(applicationEventPublisher).publishEvent(eq(new MemberDeleteEvent(CUSTOM_OAUTH2_USER.getId())));
            verify(memberRepository).delete(eq(MEMBER_ENTITY));
        });
    }

    @Test
    void testDeleteMember_MemberNotFound() {
        // given
        when(memberRepository.findById(eq(CUSTOM_OAUTH2_USER.getId()))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.deleteMember(CUSTOM_OAUTH2_USER))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
