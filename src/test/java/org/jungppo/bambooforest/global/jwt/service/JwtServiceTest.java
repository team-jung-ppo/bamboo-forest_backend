package org.jungppo.bambooforest.global.jwt.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.jsonwebtoken.JwtException;
import java.util.Optional;
import org.jungppo.bambooforest.global.jwt.domain.JwtMemberClaim;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String secretKey = "eoskantnvvmfhwprxmghkdlxld12345eoskantnvvmfhwprxmghkdlxld12345";
        int expireIn = 3600;
        this.jwtService = new JwtService(secretKey, expireIn);
    }

    @Test
    void testCreateToken() {
        // given
        JwtMemberClaim jwtMemberClaim = new JwtMemberClaim(
                1L,
                RoleType.ROLE_USER,
                OAuth2Type.OAUTH2_KAKAO
        );

        // when
        String token = jwtService.createToken(jwtMemberClaim);

        // then
        assertThat(token).isNotNull()
                .startsWith("Bearer ");
    }

    @Test
    void testParseToken() {
        // given
        JwtMemberClaim jwtMemberClaim = new JwtMemberClaim(
                1L,
                RoleType.ROLE_USER,
                OAuth2Type.OAUTH2_KAKAO
        );
        String token = jwtService.createToken(jwtMemberClaim);

        // when
        JwtMemberClaim parsedClaim = jwtService.parseToken(token);

        // then
        assertThat(parsedClaim).usingRecursiveComparison().isEqualTo(jwtMemberClaim);
    }

    @Test
    void testParseToken_InvalidToken() {
        // given
        String invalidToken = "invalidToken";

        // when & then
        assertThatThrownBy(() -> jwtService.parseToken(invalidToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void testParseOptionalToken() {
        // given
        JwtMemberClaim jwtMemberClaim = new JwtMemberClaim(
                1L,
                RoleType.ROLE_USER,
                OAuth2Type.OAUTH2_KAKAO
        );
        String token = jwtService.createToken(jwtMemberClaim);

        // when
        Optional<JwtMemberClaim> optionalClaim = jwtService.parseOptionalToken(token);

        // then
        assertThat(optionalClaim).isPresent()
                .get().usingRecursiveComparison().isEqualTo(jwtMemberClaim);
    }

    @Test
    void testParseOptionalToken_InvalidToken() {
        // given
        String invalidToken = "invalidToken";

        // when
        Optional<JwtMemberClaim> optionalClaim = jwtService.parseOptionalToken(invalidToken);

        // then
        assertThat(optionalClaim).isEmpty();
    }
}
