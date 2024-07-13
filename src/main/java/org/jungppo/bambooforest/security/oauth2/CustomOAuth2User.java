package org.jungppo.bambooforest.security.oauth2;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class CustomOAuth2User implements OAuth2User, Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String roleType;
    private final String registrationId;

    public Long getId() {
        return id;
    }

    public RoleType getRoleType() {
        return RoleType.valueOf(roleType);
    }

    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public String getName() {
        return id.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(roleType));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
}
