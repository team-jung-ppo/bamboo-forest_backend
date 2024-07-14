package org.jungppo.bambooforest.dto.member;

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
