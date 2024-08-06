package org.jungppo.bambooforest.global.client.oauth2;

import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.oauth2.dto.UnlinkResponse;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;

public abstract class OAuth2Client {
    public abstract ClientResponse<UnlinkResponse> unlink(String identifier);

    protected abstract OAuth2Type getSupportedProvider();

    public final boolean supports(OAuth2Type provider) {
        return getSupportedProvider().equals(provider);
    }
}
