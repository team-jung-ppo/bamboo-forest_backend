package org.jungppo.bambooforest.member.domain;

import org.jungppo.bambooforest.entity.common.JpaBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity extends JpaBaseEntity {

	@Id
	@Column(name = "refresh_token_id")
	private Long id;

	@Column(nullable = false)
	private String value;

	@Builder
	public RefreshTokenEntity(Long id, String value) {
		this.id = id;
		this.value = value;
	}

	public void updateValue(String value) {
		this.value = value;
	}
}
