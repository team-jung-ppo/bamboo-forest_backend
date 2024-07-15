package org.jungppo.bambooforest.member.domain.repository;

import java.util.Optional;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.dto.MemberDto;

public interface QuerydslMemberRepository {
    Optional<MemberEntity> findByName(String name);

    Optional<MemberDto> findDtoById(Long id);
}
