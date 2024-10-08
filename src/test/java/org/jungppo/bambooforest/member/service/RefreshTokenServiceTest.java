package org.jungppo.bambooforest.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jungppo.bambooforest.global.jwt.fixture.JwtMemberClaimFixture.createJwtMemberClaim;
import static org.jungppo.bambooforest.member.fixture.RefreshTokenEntityFixture.createRefreshTokenEntity;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Optional;
import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.member.domain.entity.RefreshTokenEntity;
import org.jungppo.bambooforest.member.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void testSaveOrUpdateRefreshToken() {
        // given
        final String refreshToken = "refreshToken";
        final JwtMemberClaim jwtMemberClaim = createJwtMemberClaim(1L, null, null);
        final RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(jwtMemberClaim.getId(), refreshToken);

        // when
        refreshTokenService.saveOrUpdateRefreshToken(jwtMemberClaim.getId(), refreshToken);

        // then
        then(refreshTokenRepository).should().save(eq(refreshTokenEntity));
    }

    @Test
    void testFindById() {
        // given
        final JwtMemberClaim jwtMemberClaim = createJwtMemberClaim(1L, null, null);
        final RefreshTokenEntity refreshTokenEntity = createRefreshTokenEntity(jwtMemberClaim.getId(), "refreshToken");

        given(refreshTokenRepository.findById(eq(jwtMemberClaim.getId()))).willReturn(Optional.of(refreshTokenEntity));

        // when
        final Optional<RefreshTokenEntity> foundToken = refreshTokenService.findById(jwtMemberClaim.getId());

        // then
        assertThat(foundToken.get()).isEqualTo(refreshTokenEntity);
    }

    @Test
    void testDeleteById() {
        // given
        final JwtMemberClaim jwtMemberClaim = createJwtMemberClaim(1L, null, null);

        // when
        refreshTokenService.deleteById(jwtMemberClaim.getId());

        // then
        then(refreshTokenRepository).should().deleteById(eq(jwtMemberClaim.getId()));
    }
}
