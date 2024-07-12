package org.jungppo.bambooforest.service.member;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.entity.member.RefreshTokenEntity;
import org.jungppo.bambooforest.repository.member.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * RefreshTokenEntity의 ID는 UserID와 동일함.
     * 동일한 ID를 가진 엔티티가 존재하면, save 메서드는 엔티티를 업데이트함.
     * 동일한 ID를 가진 엔티티가 존재하지 않으면, save 메서드는 엔티티를 저장함.
     */
    @Transactional
    public void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(userId)
                .value(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
    }

    public Optional<RefreshTokenEntity> findById(Long userId) {
        return refreshTokenRepository.findById(userId);
    }

    @Transactional
    public void deleteById(Long userId) {
        refreshTokenRepository.deleteById(userId);
    }
}
