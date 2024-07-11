package org.jungppo.bambooforest.security.oauth2.response;

import java.util.Map;

import static org.jungppo.bambooforest.security.oauth2.constants.GitHubConstants.GITHUB_REGISTRATION_ID;
import static org.jungppo.bambooforest.security.oauth2.constants.KakaoConstants.KAKAO_REGISTRATION_ID;

public class OAuth2ResponseFactory {
    public static OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case KAKAO_REGISTRATION_ID -> new KakaoResponse(attributes);
            case GITHUB_REGISTRATION_ID -> new GitHubResponse(attributes);
            default -> throw new Oauth2LoginFailureException("Unsupported provider: " + registrationId);
        };
    }
}
