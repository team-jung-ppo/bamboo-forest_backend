package org.jungppo.bambooforest.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.jungppo.bambooforest.entity.type.OAuth2Type;
import org.jungppo.bambooforest.entity.type.RoleType;
import java.time.LocalDateTime;

@Data
public class MemberDto {
    private Long id;
    private OAuth2Type oAuth2;
    private String username;
    private String profileImage;
    private RoleType role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public MemberDto(Long id, OAuth2Type oAuth2, String username, String profileImage, RoleType role, LocalDateTime createdAt) {
        this.id = id;
        this.oAuth2 = oAuth2;
        this.username = username;
        this.profileImage = profileImage;
        this.role = role;
        this.createdAt = createdAt;
    }
}
