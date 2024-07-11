package org.jungppo.bambooforest.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class JwtAuthentication implements Authentication {

    private final Long userId;
    private final RoleType roleType;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.roleType.name()));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return new JwtUserClaim(this.userId, this.roleType);
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // Do nothing or throw exception
    }

    @Override
    public String getName() {
        return null;
    }
}
