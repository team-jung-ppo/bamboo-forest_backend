package org.jungppo.bambooforest.service.member;

import org.jungppo.bambooforest.dto.member.JwtDto;
import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.entity.member.RefreshTokenEntity;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.repository.member.MemberRepository;
import org.jungppo.bambooforest.response.exception.member.InvalidRefreshTokenException;
import org.jungppo.bambooforest.response.exception.member.MemberNotFoundException;
import org.jungppo.bambooforest.response.exception.member.RefreshTokenFailureException;
import org.jungppo.bambooforest.security.jwt.JwtMemberClaim;
import org.jungppo.bambooforest.util.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.jungppo.bambooforest.config.JwtConfig.JWT_ACCESS_TOKEN_UTILS;
import static org.jungppo.bambooforest.config.JwtConfig.JWT_REFRESH_TOKEN_UTILS;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;  // 같은 도메인이기 때문에 의존 가능
    private final JwtUtils jwtAccessTokenUtils;
    private final JwtUtils jwtRefreshTokenUtils;

    public MemberService(MemberRepository memberRepository, RefreshTokenService refreshTokenService, @Qualifier(JWT_ACCESS_TOKEN_UTILS) JwtUtils jwtAccessTokenUtils, @Qualifier(JWT_REFRESH_TOKEN_UTILS) JwtUtils jwtRefreshTokenUtils) {
        this.memberRepository = memberRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
        this.jwtRefreshTokenUtils = jwtRefreshTokenUtils;
    }

    public MemberDto getProfile(Long memberId) {
        return memberRepository.findDtoById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    @Transactional
    public JwtDto reissuanceToken(String refreshToken) throws InvalidRefreshTokenException { // 협업의 원활함을 위하여 Header, Cookie가 아닌 Body로 반환
        JwtMemberClaim jwtMemberClaim = jwtRefreshTokenUtils.parseOptionalToken(refreshToken).orElseThrow(RefreshTokenFailureException::new);
        Long id = jwtMemberClaim.getId();
        RoleType role = jwtMemberClaim.getRole();
        String registrationId = jwtMemberClaim.getRegistrationId();

        validateRefreshToken(id, refreshToken);
        String newAccessToken = jwtAccessTokenUtils.createToken(new JwtMemberClaim(id, role, registrationId));
        String newRefreshToken = jwtRefreshTokenUtils.createToken(new JwtMemberClaim(id, role, registrationId));

        refreshTokenService.saveOrUpdateRefreshToken(id, newRefreshToken);
        return new JwtDto(newAccessToken, newRefreshToken);
    }

    private void validateRefreshToken(Long id, String refreshToken) throws InvalidRefreshTokenException {  //  CheckedException을 통해 트랜잭션 발생하지 않으면서 오류 메세지 출력
        RefreshTokenEntity existingToken = refreshTokenService.findById(id).orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found."));
        if (!existingToken.getValue().equals(refreshToken)) {
            refreshTokenService.deleteById(id);
            throw new InvalidRefreshTokenException("Invalid refresh token.");
        }
    }
}
