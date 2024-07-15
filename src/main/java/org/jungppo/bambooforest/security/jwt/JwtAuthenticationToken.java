package org.jungppo.bambooforest.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken implements Authentication {

    private final String token;
    private boolean authenticated = false;

    public JwtAuthenticationToken(String token) {
        this.token = token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        throw new UnsupportedOperationException("getAuthorities() is not supported");
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        throw new UnsupportedOperationException("getPrincipal() is not supported");
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName() is not supported");
    }
}