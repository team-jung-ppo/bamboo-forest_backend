package org.jungppo.bambooforest.security.oauth2.response;

import org.jungppo.bambooforest.entity.type.OAuth2Type;

public interface OAuth2Response {
    OAuth2Type getProvider();
    String getProviderId();
    String getName();
    String getProfileImage();
}
