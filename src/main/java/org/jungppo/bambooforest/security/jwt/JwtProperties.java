package org.jungppo.bambooforest.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private AccessToken accessToken;
    private RefreshToken refreshToken;

    @Data
    public static class AccessToken {
        private  String secretKey;
        private  int expireIn;
    }

    @Data
    public static class RefreshToken {
        private  String secretKey;
        private  int expireIn;
    }
}
