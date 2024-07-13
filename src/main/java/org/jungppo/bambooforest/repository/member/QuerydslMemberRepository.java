package org.jungppo.bambooforest.repository.member;

import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.entity.member.MemberEntity;

import java.util.Optional;

public interface QuerydslMemberRepository {
    Optional<MemberEntity> findByName(String name);
    Optional<MemberDto> findDtoById(Long id);
}
