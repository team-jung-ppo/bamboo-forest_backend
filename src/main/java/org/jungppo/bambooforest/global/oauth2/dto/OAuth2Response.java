package org.jungppo.bambooforest.global.oauth2.dto;

import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;

public interface OAuth2Response {
    OAuth2Type getProvider();

    String getProviderId();

    String getName();

    String getProfileImage();
}
