package org.jungppo.bambooforest.member.fixture;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;
import static org.jungppo.bambooforest.member.domain.entity.RoleType.ROLE_USER;

import java.time.LocalDateTime;
import java.util.List;
import org.jungppo.bambooforest.chatbot.fixture.ChatBotItemDtoFixture;
import org.jungppo.bambooforest.member.dto.MemberDto;

public class MemberDtoFixture {

    public static final MemberDto MEMBER_DTO = new MemberDto(
            1L,
            OAUTH2_KAKAO,
            "testuser",
            "https://example.com/profile.jpg",
            ROLE_USER,
            100,
            List.of(ChatBotItemDtoFixture.UNCLE_CHATBOT_DTO, ChatBotItemDtoFixture.AUNT_CHATBOT_DTO),
            LocalDateTime.now()
    );
}
