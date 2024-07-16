package org.jungppo.bambooforest.member.domain.repository;

import java.util.Optional;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;

public interface QuerydslMemberRepository {
    Optional<MemberEntity> findByName(final String name);
}
