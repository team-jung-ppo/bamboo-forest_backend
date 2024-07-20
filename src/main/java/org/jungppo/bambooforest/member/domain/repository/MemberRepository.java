package org.jungppo.bambooforest.member.domain.repository;



import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, QuerydslMemberRepository {
    Optional<MemberEntity> findByUsername(String username);
  
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select m from MemberEntity m where m.id = :id")
    Optional<MemberEntity> findByIdWithLock(@Param("id") Long id);
}
