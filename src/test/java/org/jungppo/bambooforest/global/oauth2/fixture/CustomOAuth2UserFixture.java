package org.jungppo.bambooforest.global.oauth2.fixture;

import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;

public class CustomOAuth2UserFixture {

    public static CustomOAuth2User createCustomOAuth2User(final Long Id, final RoleType roleType,
                                                          final OAuth2Type oAuth2Type) {
        return new CustomOAuth2User(Id, roleType, oAuth2Type);
    }
}
