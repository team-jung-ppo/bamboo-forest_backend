package org.jungppo.bambooforest.global.oauth2.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(OAuth2AuthorizedClientEntityId.class)
@Table(name = "oauth2_authorized_client")
public class OAuth2AuthorizedClientEntity {

    @Id
    @Column(length = 100, nullable = false)
    private String clientRegistrationId;

    @Id
    @Column(length = 200, nullable = false)
    private String principalName;

    @Column(length = 100, nullable = false)
    private String accessTokenType;

    @Column(nullable = false)
    private byte[] accessTokenValue;

    @Column(nullable = false)
    private LocalDateTime accessTokenIssuedAt;

    @Column(nullable = false)
    private LocalDateTime accessTokenExpiresAt;

    @Column(length = 1000)
    private String accessTokenScopes;

    private byte[] refreshTokenValue;

    private LocalDateTime refreshTokenIssuedAt;

    @Builder
    public OAuth2AuthorizedClientEntity(String clientRegistrationId, String principalName, String accessTokenType,
                                        byte[] accessTokenValue, LocalDateTime accessTokenIssuedAt,
                                        LocalDateTime accessTokenExpiresAt, String accessTokenScopes,
                                        byte[] refreshTokenValue, LocalDateTime refreshTokenIssuedAt) {
        this.clientRegistrationId = clientRegistrationId;
        this.principalName = principalName;
        this.accessTokenType = accessTokenType;
        this.accessTokenValue = accessTokenValue;
        this.accessTokenIssuedAt = accessTokenIssuedAt;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.accessTokenScopes = accessTokenScopes;
        this.refreshTokenValue = refreshTokenValue;
        this.refreshTokenIssuedAt = refreshTokenIssuedAt;
    }
}
