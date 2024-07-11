package org.jungppo.bambooforest.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.security.jwt.JwtUserClaim;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {
	public static final String USER_ID = "userId";
	public static final String USER_ROLE = "userRole";
	private static final long MILLI_SECOND = 1000L;

	private final SecretKey secretKey;
	private final int expireIn;

	public JwtUtils(String secretKey, int expireIn) {
		this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
		this.expireIn = expireIn;
	}

	public String createToken(JwtUserClaim jwtUserClaim) {
		Map<String, Object> tokenClaims = this.createClaims(jwtUserClaim);
		Date now = new Date(System.currentTimeMillis());
		return Jwts.builder()
			.claims(tokenClaims)
			.issuedAt(now)
			.expiration(new Date(now.getTime() + expireIn * MILLI_SECOND))
			.signWith(secretKey)
			.compact();
	}

	private Map<String, Object> createClaims(JwtUserClaim jwtUserClaim) {
		return Map.of(
			USER_ID, jwtUserClaim.getUserId(),
			USER_ROLE, jwtUserClaim.getRoleType()
		);
	}

	public JwtUserClaim parseToken(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
		return convert(claims);
	}

	public JwtUserClaim convert(Claims claims) {
		return new JwtUserClaim(
			claims.get(USER_ID, Long.class),
			claims.get(USER_ROLE, RoleType.class)
		);
	}
}
