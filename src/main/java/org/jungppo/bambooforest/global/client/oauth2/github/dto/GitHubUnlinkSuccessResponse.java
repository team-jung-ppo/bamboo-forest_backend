package org.jungppo.bambooforest.global.client.oauth2.github.dto;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.global.client.oauth2.dto.UnlinkResponse;

@RequiredArgsConstructor
public class GitHubUnlinkSuccessResponse implements UnlinkResponse {

    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
}
