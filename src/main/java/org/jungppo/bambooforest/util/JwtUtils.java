package org.jungppo.bambooforest.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.security.jwt.JwtMemberClaim;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {
	public static final String ID = "id";
	public static final String ROLE = "role";
	public static final String REGISTRATION_ID = "registrationId";
	private static final long MILLI_SECOND = 1000L;

	private final SecretKey secretKey;
	private final int expireIn;

	public JwtUtils(String secretKey, int expireIn) {
		this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
		this.expireIn = expireIn;
	}

	public String createToken(JwtMemberClaim jwtMemberClaim) {
		Map<String, Object> tokenClaims = this.createClaims(jwtMemberClaim);
		Date now = new Date(System.currentTimeMillis());
		return Jwts.builder()
			.claims(tokenClaims)
			.issuedAt(now)
			.expiration(new Date(now.getTime() + expireIn * MILLI_SECOND))
			.signWith(secretKey)
			.compact();
	}

	private Map<String, Object> createClaims(JwtMemberClaim jwtMemberClaim) {
		return Map.of(
				ID, jwtMemberClaim.getId(),
				ROLE, jwtMemberClaim.getRole().name(),
				REGISTRATION_ID, jwtMemberClaim.getRegistrationId()
		);
	}

	public JwtMemberClaim parseToken(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return convert(claims);
	}

	public JwtMemberClaim convert(Claims claims) {
		return new JwtMemberClaim(
				claims.get(ID, Long.class),
				RoleType.valueOf(claims.get(ROLE, String.class)),
				claims.get(REGISTRATION_ID, String.class)
		);
	}
}
