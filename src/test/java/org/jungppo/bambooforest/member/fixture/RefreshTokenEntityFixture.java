package org.jungppo.bambooforest.member.fixture;

import org.jungppo.bambooforest.member.domain.entity.RefreshTokenEntity;

public class RefreshTokenEntityFixture {

    public static RefreshTokenEntity createRefreshTokenEntity(final Long memberId, final String refreshToken) {
        return new RefreshTokenEntity(memberId, refreshToken);
    }
}
