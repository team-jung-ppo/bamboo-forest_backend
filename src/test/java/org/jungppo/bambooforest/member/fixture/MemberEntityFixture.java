package org.jungppo.bambooforest.member.fixture;

import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;
import org.jungppo.bambooforest.member.domain.entity.RoleType;
import org.jungppo.bambooforest.util.ReflectionUtils;

public class MemberEntityFixture {

    public static MemberEntity createMemberEntity(final Long id, final OAuth2Type oAuth2Type, final String username,
                                                  final String profileImageUrl, final RoleType roleType) {
        final MemberEntity member = MemberEntity.of(
                (oAuth2Type != null ? oAuth2Type.name() + "_" + id : null),
                oAuth2Type,
                username,
                profileImageUrl,
                roleType
        );
        ReflectionUtils.setField(member, "id", id);
        return member;
    }
}
