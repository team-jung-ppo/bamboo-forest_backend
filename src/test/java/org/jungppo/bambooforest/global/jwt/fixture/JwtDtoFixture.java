package org.jungppo.bambooforest.global.jwt.fixture;

import org.jungppo.bambooforest.global.jwt.dto.JwtDto;

public class JwtDtoFixture {

    public static JwtDto createJwtDto(final String accessToken, final String refreshToken) {
        return new JwtDto(accessToken, refreshToken);
    }
}
