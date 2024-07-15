package org.jungppo.bambooforest.global.jwt.domain;

import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public class JwtMemberClaim {
    private final Long id;
    private final RoleType roleType;
    private final OAuth2Type oAuth2Type;

    public static class JwtAuthenticationToken implements Authentication {

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
}
