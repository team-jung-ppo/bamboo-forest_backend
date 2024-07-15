package org.jungppo.bambooforest.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {
	private String accessToken;
	private String RefreshToken;
}
