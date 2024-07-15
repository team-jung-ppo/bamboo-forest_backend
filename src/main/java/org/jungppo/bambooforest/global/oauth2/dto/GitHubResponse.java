package org.jungppo.bambooforest.global.oauth2.dto;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_GITHUB;

import java.util.Map;
import org.jungppo.bambooforest.global.oauth2.settings.GitHubConstants;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;

public class GitHubResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public GitHubResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public OAuth2Type getProvider() {
        return OAUTH2_GITHUB;
    }

    @Override
    public String getProviderId() {
        return attribute.get(GitHubConstants.GITHUB_ID).toString();
    }

    @Override
    public String getName() {
        return attribute.get(GitHubConstants.GITHUB_NICKNAME).toString();
    }

    @Override
    public String getProfileImage() {
        return attribute.get(GitHubConstants.GITHUB_PROFILE_IMAGE_URL).toString();
    }
}
