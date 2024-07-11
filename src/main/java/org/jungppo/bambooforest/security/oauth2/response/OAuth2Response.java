package org.jungppo.bambooforest.security.oauth2.response;

public interface OAuth2Response {
    String getProvider();
    String getProviderId();
    String getName();
    String getProfileImage();
}
