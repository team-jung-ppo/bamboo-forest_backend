package org.jungppo.bambooforest.global.oauth2.service;

import static org.jungppo.bambooforest.global.oauth2.setting.GitHubConstants.GITHUB_REGISTRATION_ID;
import static org.jungppo.bambooforest.global.oauth2.setting.KakaoConstants.KAKAO_REGISTRATION_ID;

import java.util.Map;
import org.jungppo.bambooforest.global.oauth2.dto.GitHubResponse;
import org.jungppo.bambooforest.global.oauth2.dto.KakaoResponse;
import org.jungppo.bambooforest.global.oauth2.dto.OAuth2Response;
import org.jungppo.bambooforest.global.oauth2.exception.OAuth2LoginFailureException;

public class OAuth2ResponseFactory {
    public static OAuth2Response getOAuth2Response(final String registrationId, final Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case KAKAO_REGISTRATION_ID -> new KakaoResponse(attributes);
            case GITHUB_REGISTRATION_ID -> new GitHubResponse(attributes);
            default -> throw new OAuth2LoginFailureException("Unsupported provider: " + registrationId);
        };
    }
}
