package org.jungppo.bambooforest.member.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity extends JpaBaseEntity {

    @Id
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "`value`", unique = true, nullable = false)
    private String value;

    @Builder
    public RefreshTokenEntity(@NonNull final Long id, @NonNull final String value) {
        this.id = id;
        this.value = value;
    }

    public void updateValue(final String value) {
        this.value = value;
    }
}
