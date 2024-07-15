package org.jungppo.bambooforest.member.dto;

import java.time.LocalDateTime;

import org.jungppo.bambooforest.entity.type.OAuth2Type;
import org.jungppo.bambooforest.entity.type.RoleType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
	private Long id;
	private OAuth2Type oAuth2;
	private String username;
	private String profileImage;
	private RoleType role;
	private int batteryCount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;

	@QueryProjection
	public MemberDto(Long id, OAuth2Type oAuth2, String username, String profileImage, RoleType role, int batteryCount,
		LocalDateTime createdAt) {
		this.id = id;
		this.oAuth2 = oAuth2;
		this.username = username;
		this.profileImage = profileImage;
		this.role = role;
		this.batteryCount = batteryCount;
		this.createdAt = createdAt;
	}
}
