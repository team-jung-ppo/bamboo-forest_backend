package org.jungppo.bambooforest.service.member;

import org.jungppo.bambooforest.dto.member.JwtDto;
import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.entity.member.RefreshTokenEntity;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.repository.member.MemberRepository;
import org.jungppo.bambooforest.repository.member.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtAccessTokenUtils;
    private final JwtUtils jwtRefreshTokenUtils;

    public MemberService(MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, @Qualifier(JWT_ACCESS_TOKEN_UTILS) JwtUtils jwtAccessTokenUtils, @Qualifier(JWT_REFRESH_TOKEN_UTILS) JwtUtils jwtRefreshTokenUtils) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
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

        saveOrUpdateRefreshToken(id, newRefreshToken);
        return new JwtDto(newAccessToken, newRefreshToken);
    }

    private void validateRefreshToken(Long id, String refreshToken) throws InvalidRefreshTokenException {  //  CheckedException을 통해 트랜잭션 발생하지 않으면서 오류 메세지 출력
        RefreshTokenEntity existingToken = refreshTokenRepository.findById(id).orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found."));
        if (!existingToken.getValue().equals(refreshToken)) {
            refreshTokenRepository.delete(existingToken);
            throw new InvalidRefreshTokenException("Invalid refresh token.");
        }
    }

    /**
     * RefreshTokenEntity의 ID는 UserID와 동일함.
     * 동일한 ID를 가진 엔티티가 존재하면, save 메서드는 엔티티를 업데이트함.
     * 동일한 ID를 가진 엔티티가 존재하지 않으면, save 메서드는 엔티티를 저장함.
     */
    private void saveOrUpdateRefreshToken(Long userId, String refreshToken) {  // TODO. 책임을 가진 객체를 이용하여 할당
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(userId)
                .value(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
    }
}
