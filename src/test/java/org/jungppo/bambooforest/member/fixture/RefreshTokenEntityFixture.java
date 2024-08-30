package org.jungppo.bambooforest.member.fixture;

import static org.jungppo.bambooforest.global.jwt.fixture.JwtDtoFixture.REFRESH_TOKEN;
import static org.jungppo.bambooforest.member.fixture.MemberEntityFixture.MEMBER_ENTITY;

import org.jungppo.bambooforest.member.domain.entity.RefreshTokenEntity;

public class RefreshTokenEntityFixture {

    public static final RefreshTokenEntity REFRESH_TOKEN_ENTITY = new RefreshTokenEntity(
            MEMBER_ENTITY.getId(),
            REFRESH_TOKEN
    );
}
