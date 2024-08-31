package org.jungppo.bambooforest.global.jwt.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
        assertSoftly(softly -> {
            softly.assertThat(token).isNotNull();
            softly.assertThat(token).startsWith("Bearer ");
        });
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
        assertSoftly(softly -> {
            softly.assertThat(parsedClaim).isNotNull();
            softly.assertThat(parsedClaim.getId()).isEqualTo(jwtMemberClaim.getId());
            softly.assertThat(parsedClaim.getRoleType()).isEqualTo(jwtMemberClaim.getRoleType());
            softly.assertThat(parsedClaim.getOAuth2Type()).isEqualTo(jwtMemberClaim.getOAuth2Type());
        });
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
        assertSoftly(softly -> {
            softly.assertThat(optionalClaim).isPresent();
            softly.assertThat(optionalClaim.get().getId()).isEqualTo(jwtMemberClaim.getId());
            softly.assertThat(optionalClaim.get().getRoleType()).isEqualTo(jwtMemberClaim.getRoleType());
            softly.assertThat(optionalClaim.get().getOAuth2Type()).isEqualTo(jwtMemberClaim.getOAuth2Type());
        });
    }

    @Test
    void testParseOptionalToken_InvalidToken() {
        // given
        String invalidToken = "invalidToken";

        // when
        Optional<JwtMemberClaim> optionalClaim = jwtService.parseOptionalToken(invalidToken);

        // then
        assertSoftly(softly -> {
            softly.assertThat(optionalClaim).isEmpty();
        });
    }
}
