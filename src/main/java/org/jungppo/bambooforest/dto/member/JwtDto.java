package org.jungppo.bambooforest.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtDto {
    private String accessToken;
    private String RefreshToken;
}
