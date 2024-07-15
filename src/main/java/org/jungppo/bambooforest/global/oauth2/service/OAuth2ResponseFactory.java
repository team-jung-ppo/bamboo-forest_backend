package org.jungppo.bambooforest.global.oauth2.service;

import java.util.Map;
import org.jungppo.bambooforest.global.oauth2.dto.GitHubResponse;
import org.jungppo.bambooforest.global.oauth2.dto.KakaoResponse;
import org.jungppo.bambooforest.global.oauth2.dto.OAuth2Response;
import org.jungppo.bambooforest.global.oauth2.exception.OAuth2LoginFailureException;
import org.jungppo.bambooforest.global.oauth2.settings.GitHubConstants;
import org.jungppo.bambooforest.global.oauth2.settings.KakaoConstants;

public class OAuth2ResponseFactory {
    public static OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case KakaoConstants.KAKAO_REGISTRATION_ID -> new KakaoResponse(attributes);
            case GitHubConstants.GITHUB_REGISTRATION_ID -> new GitHubResponse(attributes);
            default -> throw new OAuth2LoginFailureException("Unsupported provider: " + registrationId);
        };
    }
}
