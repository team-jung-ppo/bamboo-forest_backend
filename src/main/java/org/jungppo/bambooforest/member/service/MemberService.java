package org.jungppo.bambooforest.member.service;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;
import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_REFRESH_TOKEN_SERVICE;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.client.oauth2.OAuth2Client;
import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.global.jwt.dto.JwtDto;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.MemberDeleteEvent;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RefreshTokenEntity;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.MemberDto;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.member.exception.RefreshTokenFailureException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final OAuth2AuthorizedClientService jdbcOAuth2AuthorizedClientServiceProxy;
    private final RefreshTokenService refreshTokenService;  // 같은 도메인이기 때문에 의존 가능
    private final JwtService jwtAccessTokenService;
    private final JwtService jwtRefreshTokenService;
    private final List<OAuth2Client> oauth2Clients;
    private final ApplicationEventPublisher applicationEventPublisher;

    public MemberService(final MemberRepository memberRepository,
                         final OAuth2AuthorizedClientService jdbcOAuth2AuthorizedClientServiceProxy,
                         final RefreshTokenService refreshTokenService,
                         @Qualifier(JWT_ACCESS_TOKEN_SERVICE) final JwtService jwtAccessTokenService,
                         @Qualifier(JWT_REFRESH_TOKEN_SERVICE) final JwtService jwtRefreshTokenService,
                         final List<OAuth2Client> oauth2Clients,
                         final ApplicationEventPublisher applicationEventPublisher) {
        this.memberRepository = memberRepository;
        this.jdbcOAuth2AuthorizedClientServiceProxy = jdbcOAuth2AuthorizedClientServiceProxy;
        this.refreshTokenService = refreshTokenService;
        this.jwtAccessTokenService = jwtAccessTokenService;
        this.jwtRefreshTokenService = jwtRefreshTokenService;
        this.oauth2Clients = oauth2Clients;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void logout(final CustomOAuth2User customOAuth2User) {
        refreshTokenService.deleteById(customOAuth2User.getId());  // 내부적으로 ifPresent 일때만 삭제하도록 처리되어있음.
        jdbcOAuth2AuthorizedClientServiceProxy.removeAuthorizedClient(  // OAuth2 Server(Kakao, GitHub)에게 발급받은 정보들도 삭제
                customOAuth2User.getOAuth2Type().getRegistrationId(),
                customOAuth2User.getId().toString());
    }

    public MemberDto getMember(final CustomOAuth2User customOAuth2User) {
        final MemberEntity memberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);
        return MemberDto.from(memberEntity);
    }

    @Transactional(noRollbackFor = {RefreshTokenFailureException.class})
    public JwtDto reissuanceToken(final String refreshToken) { // 협업의 원활함을 위하여 Header, Cookie가 아닌 Body로 반환
        final JwtMemberClaim jwtMemberClaim = jwtRefreshTokenService.parseOptionalToken(refreshToken)
                .orElseThrow(RefreshTokenFailureException::new);
        final Long id = jwtMemberClaim.getId();
        final RoleType roleType = jwtMemberClaim.getRoleType();
        final OAuth2Type oAuth2Type = jwtMemberClaim.getOAuth2Type();

        validateRefreshToken(id, refreshToken);
        final String newAccessToken = jwtAccessTokenService.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));
        final String newRefreshToken = jwtRefreshTokenService.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));

        refreshTokenService.saveOrUpdateRefreshToken(id, newRefreshToken);
        return new JwtDto(newAccessToken, newRefreshToken);
    }

    private void validateRefreshToken(final Long id,
                                      final String refreshToken) {
        final RefreshTokenEntity existingToken = refreshTokenService.findById(id)
                .orElseThrow(RefreshTokenFailureException::new);
        if (!existingToken.getValue().equals(refreshToken)) {
            refreshTokenService.deleteById(id);
            throw new RefreshTokenFailureException();  // noRollbackFor로 인해 롤백되지 않음
        }
    }

    @Transactional
    public void deleteMember(final CustomOAuth2User customOAuth2User) {  // TODO. Pub/Sub 구조로 의존성 분리
        final MemberEntity memberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);
        applicationEventPublisher.publishEvent(new MemberDeleteEvent(memberEntity.getId()));
        unlinkOAuth2Member(memberEntity);
        memberRepository.delete(memberEntity);
    }

    private void unlinkOAuth2Member(MemberEntity memberEntity) {
        String[] parts = validateMemberName(memberEntity.getName());
        OAuth2Type provider = memberEntity.getOAuth2();
        String OAuth2MemberId = parts[2];
        OAuth2AuthorizedClient authorizedClient = jdbcOAuth2AuthorizedClientServiceProxy.loadAuthorizedClient(
                provider.getRegistrationId(), memberEntity.getId().toString());
        String identifier = getIdentifier(provider, OAuth2MemberId, authorizedClient);

        oauth2Clients.stream()
                .filter(service -> service.supports(provider))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported OAuth2 provider: " + provider.getRegistrationId()))
                .unlink(identifier)
                .getData()
                .orElseGet(() -> {
                    log.warn("No unlink response received for member ID: {} with provider: {}",
                            memberEntity.getId(), provider.getRegistrationId());
                    return null;
                });

        jdbcOAuth2AuthorizedClientServiceProxy.removeAuthorizedClient(  // OAuth2 Server(Kakao, GitHub)에게 발급받은 정보들도 삭제
                memberEntity.getOAuth2().getRegistrationId(),
                memberEntity.getId().toString());
    }

    private String[] validateMemberName(String memberName) {
        String[] parts = memberName.split("_", 3);
        if (parts.length < 3) {
            throw new RuntimeException("Invalid member name format for OAuth2 unlinking.");
        }
        return parts;
    }

    private String getIdentifier(OAuth2Type clientRegistrationId, String OAuth2MemberId,
                                 OAuth2AuthorizedClient authorizedClient) {
        switch (clientRegistrationId) {
            case OAUTH2_KAKAO:
                return OAuth2MemberId;
            case OAUTH2_GITHUB:
                return authorizedClient.getAccessToken().getTokenValue();
            default:
                throw new RuntimeException("Unsupported OAuth2 provider: " + clientRegistrationId);
        }
    }
}
