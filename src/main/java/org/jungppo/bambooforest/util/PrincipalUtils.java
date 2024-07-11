package org.jungppo.bambooforest.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.security.oauth2.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrincipalUtils {

    public static Long getUserId() {
        return getUserDetails().getId();
    }
    public static String getUserName() {
        return getUserDetails().getName();
    }

    public static RoleType getUserRole() {
        return getUserDetails().getAuthorities()
                .stream()
                .map(authority -> RoleType.valueOf(authority.getAuthority()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No roles found for the user"));
    }

    private static CustomOAuth2User getUserDetails() {
        return (CustomOAuth2User) getAuthentication().getPrincipal();
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
