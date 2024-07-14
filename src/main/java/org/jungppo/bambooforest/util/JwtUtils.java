package org.jungppo.bambooforest.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jungppo.bambooforest.entity.type.OAuth2Type;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.security.jwt.JwtMemberClaim;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtils {
	public static final String ID = "id";
	public static final String ROLE_TYPE = "roleType";
	public static final String OAUTH2_TYPE = "oAuth2Type";
	private static final long MILLI_SECOND = 1000L;
	private static final String BEARER_PREFIX = "Bearer ";

	private final SecretKey secretKey;
	private final int expireIn;

	public JwtUtils(String secretKey, int expireIn) {
		this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
		this.expireIn = expireIn;
	}

	public String createToken(JwtMemberClaim jwtMemberClaim) {
		Map<String, Object> tokenClaims = this.createClaims(jwtMemberClaim);
		Date now = new Date(System.currentTimeMillis());
		return BEARER_PREFIX + Jwts.builder()
			.claims(tokenClaims)
			.issuedAt(now)
			.expiration(new Date(now.getTime() + expireIn * MILLI_SECOND))
			.signWith(secretKey)
			.compact();
	}

	private Map<String, Object> createClaims(JwtMemberClaim jwtMemberClaim) {
		return Map.of(
			ID, jwtMemberClaim.getId(),
			ROLE_TYPE, jwtMemberClaim.getRoleType().name(),
			OAUTH2_TYPE, jwtMemberClaim.getOAuth2Type().name()
		);
	}

	public JwtMemberClaim parseToken(String token) {  // 예외를 직접 처리
		Claims claims = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(unType(token))
			.getPayload();
		return convert(claims);
	}

	public Optional<JwtMemberClaim> parseOptionalToken(String token) {  // 예외 자동 처리
		try {
			Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(unType(token))
				.getPayload();
			return Optional.of(convert(claims));
		} catch (JwtException e) {
			return Optional.empty();
		}
	}

	public JwtMemberClaim convert(Claims claims) {
		return new JwtMemberClaim(
			claims.get(ID, Long.class),
			RoleType.valueOf(claims.get(ROLE_TYPE, String.class)),
			OAuth2Type.valueOf(claims.get(OAUTH2_TYPE, String.class))
		);
	}

	public String unType(String token) {
		if (token != null && token.startsWith(BEARER_PREFIX)) {
			return token.substring(BEARER_PREFIX.length());
		}
		return token;
	}
}
