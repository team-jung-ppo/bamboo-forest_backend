package org.jungppo.bambooforest.repository.member;

import org.jungppo.bambooforest.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, QuerydslMemberRepository {
    Optional<MemberEntity> findByName(String name);
}
