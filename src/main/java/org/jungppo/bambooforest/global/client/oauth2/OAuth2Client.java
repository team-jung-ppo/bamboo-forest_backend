package org.jungppo.bambooforest.global.client.oauth2;

import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.oauth2.dto.UnlinkResponse;

public abstract class OAuth2Client {
    public abstract ClientResponse<UnlinkResponse> unlink(String identifier);

    protected abstract String getSupportedProvider();

    public final boolean supports(String provider) {
        return getSupportedProvider().equalsIgnoreCase(provider);
    }
}
