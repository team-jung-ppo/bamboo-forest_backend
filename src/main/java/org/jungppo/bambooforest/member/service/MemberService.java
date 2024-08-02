package org.jungppo.bambooforest.member.service;

import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_ACCESS_TOKEN_SERVICE;
import static org.jungppo.bambooforest.global.config.JwtConfig.JWT_REFRESH_TOKEN_SERVICE;

import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.global.jwt.dto.JwtDto;
import org.jungppo.bambooforest.global.jwt.service.JwtService;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.global.oauth2.service.CustomJdbcOAuth2AuthorizedClientService;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RefreshTokenEntity;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.dto.MemberDto;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.jungppo.bambooforest.member.exception.RefreshTokenFailureException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CustomJdbcOAuth2AuthorizedClientService customJdbcOAuth2AuthorizedClientService;
    private final RefreshTokenService refreshTokenService;  // 같은 도메인이기 때문에 의존 가능
    private final JwtService jwtAccessTokenService;
    private final JwtService jwtRefreshTokenService;

    public MemberService(final MemberRepository memberRepository,
                         final CustomJdbcOAuth2AuthorizedClientService customJdbcOAuth2AuthorizedClientService,
                         final RefreshTokenService refreshTokenService,
                         @Qualifier(JWT_ACCESS_TOKEN_SERVICE) final JwtService jwtAccessTokenService,
                         @Qualifier(JWT_REFRESH_TOKEN_SERVICE) final JwtService jwtRefreshTokenService) {
        this.memberRepository = memberRepository;
        this.customJdbcOAuth2AuthorizedClientService = customJdbcOAuth2AuthorizedClientService;
        this.refreshTokenService = refreshTokenService;
        this.jwtAccessTokenService = jwtAccessTokenService;
        this.jwtRefreshTokenService = jwtRefreshTokenService;
    }

    @Transactional
    public void logout(final CustomOAuth2User customOAuth2User) {
        refreshTokenService.deleteById(customOAuth2User.getId());  // 내부적으로 ifPresent 일때만 삭제하도록 처리되어있음.
        customJdbcOAuth2AuthorizedClientService.removeAuthorizedClient(  // OAuth2 Server(Kakao, GitHub)에게 발급받은 정보들도 삭제
                customOAuth2User.getOAuth2Type().getRegistrationId(),
                customOAuth2User.getId().toString());
    }

    public MemberDto getProfile(final CustomOAuth2User customOAuth2User) {
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
}
