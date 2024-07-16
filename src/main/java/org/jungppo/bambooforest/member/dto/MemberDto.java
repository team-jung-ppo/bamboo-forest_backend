package org.jungppo.bambooforest.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;

@Getter
public class MemberDto {
    private final Long id;
    private final OAuth2Type oAuth2;
    private final String username;
    private final String profileImage;
    private final RoleType role;
    private final int batteryCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @QueryProjection
    public MemberDto(final Long id, final OAuth2Type oAuth2, final String username, final String profileImage,
                     final RoleType role, final int batteryCount,
                     final LocalDateTime createdAt) {
        this.id = id;
        this.oAuth2 = oAuth2;
        this.username = username;
        this.profileImage = profileImage;
        this.role = role;
        this.batteryCount = batteryCount;
        this.createdAt = createdAt;
    }

    public static MemberDto from(final MemberEntity memberEntity) {
        return new MemberDto(
                memberEntity.getId(),
                memberEntity.getOAuth2(),
                memberEntity.getUsername(),
                memberEntity.getProfileImage(),
                memberEntity.getRole(),
                memberEntity.getBatteryCount(),
                memberEntity.getCreatedAt()
        );
    }
}
