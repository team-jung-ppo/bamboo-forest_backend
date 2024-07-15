package org.jungppo.bambooforest.global.jwt.settings;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private AccessTokenProperties accessTokenProperties;
    private RefreshTokenProperties refreshTokenProperties;

    @Data
    public static class AccessTokenProperties {
        private String secretKey;
        private int expireIn;
    }

    @Data
    public static class RefreshTokenProperties {
        private String secretKey;
        private int expireIn;
    }
}
