package org.jungppo.bambooforest.global.oauth2.domain.entity;

import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuth2AuthorizedClientEntityId implements Serializable {
    private String clientRegistrationId;
    private String principalName;
}
