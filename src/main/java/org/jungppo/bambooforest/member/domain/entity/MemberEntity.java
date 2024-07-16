package org.jungppo.bambooforest.member.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuth2Type oAuth2;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private int batteryCount;

    @Builder
    public MemberEntity(@NonNull final String name, @NonNull final OAuth2Type oAuth2, @NonNull final String username,
                        @NonNull final String profileImage, @NonNull final RoleType role, final int batteryCount) {
        this.name = name;
        this.oAuth2 = oAuth2;
        this.username = username;
        this.profileImage = profileImage;
        this.role = role;
        this.batteryCount = batteryCount;
    }

    public void updateInfo(final String username, final String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public void addBatteries(final int count) {
        this.batteryCount += count;
    }

    public void subtractBatteries(final int count) {
        if (this.batteryCount < count) {
            throw new IllegalStateException("Not enough batteries available.");
        }
        this.batteryCount -= count;
    }
}
