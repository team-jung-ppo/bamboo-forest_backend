package org.jungppo.bambooforest.member.domain.repository;

import java.util.Optional;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.dto.MemberDto;

public interface QuerydslMemberRepository {
    Optional<MemberEntity> findByName(final String name);

    Optional<MemberDto> findDtoById(final Long id);
}
