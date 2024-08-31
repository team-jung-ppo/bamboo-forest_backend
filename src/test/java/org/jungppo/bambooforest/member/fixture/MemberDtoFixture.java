package org.jungppo.bambooforest.member.fixture;

import java.time.LocalDateTime;
import java.util.List;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.member.dto.MemberDto;

public class MemberDtoFixture {

    public static MemberDto createMemberDto(final Long id, final OAuth2Type oAuth2Type, final String username,
                                            final String profileImageUrl, final RoleType roleType,
                                            final int batteryCount, final List<ChatBotItemDto> chatBotItems) {
        return new MemberDto(
                id,
                oAuth2Type,
                username,
                profileImageUrl,
                roleType,
                batteryCount,
                chatBotItems,
                LocalDateTime.now()
        );
    }
}
