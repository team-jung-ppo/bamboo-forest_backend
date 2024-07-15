package org.jungppo.bambooforest.member.service;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;
import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_REFRESH_TOKEN_SERVICE;

import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.global.jwt.dto.JwtDto;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.global.oauth2.domain.entity.OAuth2AuthorizedClientEntityId;
import org.jungppo.bambooforest.global.oauth2.domain.repository.OAuth2AuthorizedClientRepository;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RefreshTokenEntity;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.MemberDto;
import org.jungppo.bambooforest.member.exception.InvalidRefreshTokenException;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.member.exception.RefreshTokenFailureException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    private final RefreshTokenService refreshTokenService;  // 같은 도메인이기 때문에 의존 가능
    private final JwtService jwtAccessTokenService;
    private final JwtService jwtRefreshTokenService;

    public MemberService(final MemberRepository memberRepository,
                         final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
                         final RefreshTokenService refreshTokenService,
                         @Qualifier(JWT_ACCESS_TOKEN_SERVICE) final JwtService jwtAccessTokenService,
                         @Qualifier(JWT_REFRESH_TOKEN_SERVICE) final JwtService jwtRefreshTokenService) {
        this.memberRepository = memberRepository;
        this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtAccessTokenService = jwtAccessTokenService;
        this.jwtRefreshTokenService = jwtRefreshTokenService;
    }

    @Transactional
    public void logout(final CustomOAuth2User customOAuth2User) {
        refreshTokenService.deleteById(customOAuth2User.getId());  // 내부적으로 ifPresent 일때만 삭제하도록 처리되어있음.
        oAuth2AuthorizedClientRepository.deleteById(
                new OAuth2AuthorizedClientEntityId(customOAuth2User.getOAuth2Type().getRegistrationId(),
                        customOAuth2User.getId().toString()));  // OAuth2 Server(Kakao, GitHub)에게 발급받은 정보들도 삭제
    }

    public MemberDto getProfile(final CustomOAuth2User customOAuth2User) {
        return memberRepository.findDtoById(customOAuth2User.getId()).orElseThrow(MemberNotFoundException::new);
    }

    @Transactional
    public JwtDto reissuanceToken(final String refreshToken)
            throws InvalidRefreshTokenException { // 협업의 원활함을 위하여 Header, Cookie가 아닌 Body로 반환
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

    private void validateRefreshToken(final Long id, final String refreshToken)
            throws InvalidRefreshTokenException {  //  CheckedException을 통해 트랜잭션 발생하지 않으면서 오류 메세지 출력
        final RefreshTokenEntity existingToken = refreshTokenService.findById(id)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found."));
        if (!existingToken.getValue().equals(refreshToken)) {
            refreshTokenService.deleteById(id);
            throw new InvalidRefreshTokenException("Invalid refresh token.");
        }
    }
}
