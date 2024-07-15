package org.jungppo.bambooforest.global.oauth2.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
