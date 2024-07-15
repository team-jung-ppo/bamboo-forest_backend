package org.jungppo.bambooforest.global.jwt.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private AccessTokenProperties accessTokenProperties;
    private RefreshTokenProperties refreshTokenProperties;

    @Setter
    @Getter
    public static class AccessTokenProperties {
        private String secretKey;
        private int expireIn;
    }

    @Setter
    @Getter
    public static class RefreshTokenProperties {
        private String secretKey;
        private int expireIn;
    }
}
