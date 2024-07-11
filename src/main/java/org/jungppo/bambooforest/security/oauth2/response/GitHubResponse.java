package org.jungppo.bambooforest.security.oauth2.response;

import java.util.Map;

import static org.jungppo.bambooforest.entity.type.OAuth2Type.OAUTH2_GITHUB;
import static org.jungppo.bambooforest.security.oauth2.constants.GitHubConstants.*;

public class GitHubResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public GitHubResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return OAUTH2_GITHUB.name();
    }

    @Override
    public String getProviderId() {
        return attribute.get(GITHUB_ID).toString();
    }

    @Override
    public String getName() {
        return attribute.get(GITHUB_NICKNAME).toString();
    }

    @Override
    public String getProfileImage() {
        return attribute.get(GITHUB_PROFILE_IMAGE_URL).toString();
    }
}
