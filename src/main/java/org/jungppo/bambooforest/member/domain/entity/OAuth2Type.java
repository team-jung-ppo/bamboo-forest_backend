package org.jungppo.bambooforest.member.domain.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OAuth2Type {

    OAUTH2_KAKAO("kakao"),
    OAUTH2_GITHUB("github");

    private final String registrationId;
    private static final Map<String, OAuth2Type> REGISTRATION_MAP;

    static {
        REGISTRATION_MAP = Collections.unmodifiableMap(Arrays.stream(OAuth2Type.values())
                .collect(Collectors.toMap(OAuth2Type::getRegistrationId, Function.identity())));
    }

    public static Optional<OAuth2Type> findByRegistrationId(final String registrationId) {
        return Optional.ofNullable(REGISTRATION_MAP.get(registrationId));
    }
}
