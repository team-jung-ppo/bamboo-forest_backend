package org.jungppo.bambooforest.global.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;

@Slf4j
public class JwtService {
    public static final String ID = "id";
    public static final String ROLE_TYPE = "roleType";
    public static final String OAUTH2_TYPE = "oAuth2Type";
    private static final long MILLI_SECOND = 1000L;
    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey secretKey;
    private final int expireIn;

    public JwtService(final String secretKey, final int expireIn) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        this.expireIn = expireIn;
    }

    public String createToken(final JwtMemberClaim jwtMemberClaim) {
        final Map<String, Object> tokenClaims = this.createClaims(jwtMemberClaim);
        final Date now = new Date(System.currentTimeMillis());
        return BEARER_PREFIX + Jwts.builder()
                .claims(tokenClaims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireIn * MILLI_SECOND))
                .signWith(secretKey)
                .compact();
    }

    private Map<String, Object> createClaims(final JwtMemberClaim jwtMemberClaim) {
        return Map.of(
                ID, jwtMemberClaim.getId(),
                ROLE_TYPE, jwtMemberClaim.getRoleType().name(),
                OAUTH2_TYPE, jwtMemberClaim.getOAuth2Type().name()
        );
    }

    public JwtMemberClaim parseToken(final String token) {  // 사용하는 곳에서 예외 catch
        final Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(unType(token))
                .getPayload();
        return convert(claims);
    }

    public Optional<JwtMemberClaim> parseOptionalToken(final String token) {  // 사용하는 곳에서 예외 throw
        try {
            final Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(unType(token))
                    .getPayload();
            return Optional.of(convert(claims));
        } catch (final JwtException e) {
            return Optional.empty();
        }
    }

    private JwtMemberClaim convert(final Claims claims) {
        return new JwtMemberClaim(
                claims.get(ID, Long.class),
                RoleType.valueOf(claims.get(ROLE_TYPE, String.class)),
                OAuth2Type.valueOf(claims.get(OAUTH2_TYPE, String.class))
        );
    }

    private String unType(final String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }
}
