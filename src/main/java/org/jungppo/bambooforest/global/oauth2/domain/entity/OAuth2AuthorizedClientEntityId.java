package org.jungppo.bambooforest.global.oauth2.domain.entity;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuth2AuthorizedClientEntityId implements Serializable {
    private String clientRegistrationId;
    private String principalName;
}
