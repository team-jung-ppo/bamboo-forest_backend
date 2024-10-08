package org.jungppo.bambooforest.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.jungppo.bambooforest.global.jwt.fixture.JwtDtoFixture.createJwtDto;
import static org.jungppo.bambooforest.global.jwt.fixture.JwtMemberClaimFixture.createJwtMemberClaim;
import static org.jungppo.bambooforest.global.oauth2.fixture.CustomOAuth2UserFixture.createCustomOAuth2User;
import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;
import static org.jungppo.bambooforest.member.domain.entity.RoleType.ROLE_USER;
import static org.jungppo.bambooforest.member.fixture.MemberDtoFixture.createMemberDto;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.createMemberEntity;
import static org.jungppo.bambooforest.member.fixture.RefreshTokenEntityFixture.createRefreshTokenEntity;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.oauth2.OAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.github.GitHubOAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.kakao.KakaoOAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.kakao.dto.KakaoUnlinkSuccessResponse;
import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.global.jwt.dto.JwtDto;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.MemberDeleteEvent;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
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
    private final List<OAuth2Client> oAuth2Clients = new ArrayList<>();

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
        // given
        final CustomOAuth2User customOAuth2User = createCustomOAuth2User(1L, null, OAUTH2_KAKAO);

        // when
        memberService.logout(customOAuth2User);

        // then
        then(refreshTokenService).should().deleteById(eq(customOAuth2User.getId()));
        then(jdbcOAuth2AuthorizedClientServiceProxy).should().removeAuthorizedClient(
                eq(customOAuth2User.getOAuth2Type().getRegistrationId()),
                eq(customOAuth2User.getId().toString()));
    }

    @Test
    void testGetMember() {
        // given
        final CustomOAuth2User customOAuth2User = createCustomOAuth2User(1L, null, null);
        final MemberDto memberDto = createMemberDto(customOAuth2User.getId(), customOAuth2User.getOAuth2Type(),
                "username", "profileImageUrl", customOAuth2User.getRoleType(), 0, null);

        given(memberRepository.findById(eq(customOAuth2User.getId())))
                .willReturn(Optional.of(createMemberEntity(memberDto.getId(), memberDto.getOAuth2(),
                        memberDto.getUsername(), memberDto.getProfileImage(), memberDto.getRole())));

        // when
        final MemberDto retrievedMemberDto = memberService.getMember(customOAuth2User);

        // then
        assertThat(retrievedMemberDto)
                .usingRecursiveComparison()
                .ignoringFields("chatBots", "createdAt")
                .isEqualTo(memberDto);
    }

    @Test
    void testGetMember_MemberNotFound() {
        // given
        final CustomOAuth2User customOAuth2User = createCustomOAuth2User(1L, null, null);

        given(memberRepository.findById(eq(customOAuth2User.getId()))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.getMember(customOAuth2User))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void testReissuanceToken() {
        // given
        final JwtDto jwtDto = createJwtDto("accessToken", "refreshToken");
        final JwtMemberClaim jwtMemberClaim = createJwtMemberClaim(1L, null, null);

        given(jwtRefreshTokenService.parseOptionalToken(eq(jwtDto.getRefreshToken())))
                .willReturn(Optional.of(jwtMemberClaim));
        given(refreshTokenService.findById(eq(jwtMemberClaim.getId())))
                .willReturn(Optional.of(createRefreshTokenEntity(jwtMemberClaim.getId(), jwtDto.getRefreshToken())));
        given(jwtAccessTokenService.createToken(eq(jwtMemberClaim)))
                .willReturn(jwtDto.getAccessToken());
        given(jwtRefreshTokenService.createToken(eq(jwtMemberClaim)))
                .willReturn(jwtDto.getRefreshToken());

        // when
        final JwtDto retrievedJwtDto = memberService.reissuanceToken(jwtDto.getRefreshToken());

        // then
        then(refreshTokenService).should().saveOrUpdateRefreshToken(eq(1L), eq(jwtDto.getRefreshToken()));
        assertThat(retrievedJwtDto).usingRecursiveComparison().isEqualTo(jwtDto);
    }

    @Test
    void testReissuanceToken_RefreshTokenInvalid() {
        // given
        final JwtDto jwtDto = createJwtDto("accessToken", "refreshToken");

        given(jwtRefreshTokenService.parseOptionalToken(eq(jwtDto.getRefreshToken()))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.reissuanceToken(jwtDto.getRefreshToken()))
                .isInstanceOf(RefreshTokenFailureException.class);
    }

    @Test
    void testDeleteMember() {
        // given
        final CustomOAuth2User customOAuth2User = createCustomOAuth2User(1L, ROLE_USER, OAUTH2_KAKAO);
        final MemberEntity memberEntity = createMemberEntity(customOAuth2User.getId(), OAUTH2_KAKAO, "username",
                "profileImageUrl", null);
        final String oauth2MemberId = memberEntity.getName().split("_", 3)[2];

        given(memberRepository.findById(eq(customOAuth2User.getId()))).willReturn(Optional.of(memberEntity));
        given(kakaoOAuth2Client.supports(OAUTH2_KAKAO)).willReturn(true);
        given(kakaoOAuth2Client.unlink(eq(oauth2MemberId))).willReturn(
                ClientResponse.success(new KakaoUnlinkSuccessResponse("Successfully unlinked Kakao account.")));

        // when
        memberService.deleteMember(customOAuth2User);

        // then
        then(jdbcOAuth2AuthorizedClientServiceProxy).should().removeAuthorizedClient(
                eq(customOAuth2User.getOAuth2Type().getRegistrationId()), eq(customOAuth2User.getId().toString()));
        then(applicationEventPublisher).should().publishEvent(eq(new MemberDeleteEvent(customOAuth2User.getId())));
        then(memberRepository).should().delete(eq(memberEntity));
    }

    @Test
    void testDeleteMember_MemberNotFound() {
        // given
        final CustomOAuth2User customOAuth2User = createCustomOAuth2User(1L, null, null);

        given(memberRepository.findById(eq(customOAuth2User.getId()))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.deleteMember(customOAuth2User))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
