package org.jungppo.bambooforest.member.domain.repository;

import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, QuerydslMemberRepository {
    
}
