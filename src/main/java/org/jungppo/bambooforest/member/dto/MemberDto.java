package org.jungppo.bambooforest.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;

@Getter
@RequiredArgsConstructor
public class MemberDto {
    private final Long id;
    private final OAuth2Type oAuth2;
    private final String username;
    private final String profileImage;
    private final RoleType role;
    private final int batteryCount;
    private final List<ChatBotItemDto> chatBots;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    public static MemberDto from(final MemberEntity memberEntity) {
        return new MemberDto(
                memberEntity.getId(),
                memberEntity.getOAuth2(),
                memberEntity.getUsername(),
                memberEntity.getProfileImage(),
                memberEntity.getRole(),
                memberEntity.getBatteryCount(),
                memberEntity.getChatBots().stream().map(ChatBotItemDto::from).toList(),
                memberEntity.getCreatedAt()
        );
    }
}
