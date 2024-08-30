package org.jungppo.bambooforest.member.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.member.domain.entity.RefreshTokenEntity;
import org.jungppo.bambooforest.member.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * @formatter:off
     * RefreshTokenEntity의 ID는 UserID와 동일함. 동일한 ID를 가진 엔티티가 존재하면,
     * save 메서드는 엔티티를 업데이트함.
     * 동일한 ID를 가진 엔티티가 존재하지 않으면, save 메서드는 엔티티를 저장함.
     * @formatter:on
     */
    @Transactional
    public void saveOrUpdateRefreshToken(final Long userId, final String refreshToken) {
        final RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(userId)
                .value(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
    }

    public Optional<RefreshTokenEntity> findById(final Long userId) {
        return refreshTokenRepository.findById(userId);
    }

    @Transactional
    public void deleteById(final Long userId) {
        refreshTokenRepository.deleteById(userId);
    }
}
