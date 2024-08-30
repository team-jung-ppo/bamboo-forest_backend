package org.jungppo.bambooforest.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jungppo.bambooforest.global.jwt.fixture.JwtDtoFixture.REFRESH_TOKEN;
import static org.jungppo.bambooforest.global.jwt.fixture.JwtMemberClaimFixture.JWT_MEMBER_CLAIM;
import static org.jungppo.bambooforest.member.fixture.RefreshTokenEntityFixture.REFRESH_TOKEN_ENTITY;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
        // given & when
        refreshTokenService.saveOrUpdateRefreshToken(JWT_MEMBER_CLAIM.getId(), REFRESH_TOKEN);

        // then
        verify(refreshTokenRepository).save(eq(REFRESH_TOKEN_ENTITY));
    }

    @Test
    void testFindById() {
        // given
        when(refreshTokenRepository.findById(eq(JWT_MEMBER_CLAIM.getId()))).thenReturn(
                Optional.of(REFRESH_TOKEN_ENTITY));

        // when
        Optional<RefreshTokenEntity> foundToken = refreshTokenService.findById(JWT_MEMBER_CLAIM.getId());

        // then
        assertThat(foundToken.get()).isEqualTo(REFRESH_TOKEN_ENTITY);
        verify(refreshTokenRepository).findById(eq(JWT_MEMBER_CLAIM.getId()));
    }

    @Test
    void testDeleteById() {
        // given & when
        refreshTokenService.deleteById(JWT_MEMBER_CLAIM.getId());

        // then
        verify(refreshTokenRepository).deleteById(eq(JWT_MEMBER_CLAIM.getId()));
    }
}
