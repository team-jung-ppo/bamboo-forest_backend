package org.jungppo.bambooforest.member.fixture;

import org.jungppo.bambooforest.global.jwt.dto.JwtDto;

public class JwtDtoFixture {

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String REFRESH_TOKEN = "refreshToken";

    public static final JwtDto JWT_DTO = new JwtDto(ACCESS_TOKEN, REFRESH_TOKEN);
}
