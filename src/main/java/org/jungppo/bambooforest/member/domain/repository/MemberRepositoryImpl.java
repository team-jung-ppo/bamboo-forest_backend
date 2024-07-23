package org.jungppo.bambooforest.member.domain.repository;

import static org.jungppo.bambooforest.member.domain.entity.QMemberEntity.memberEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements QuerydslMemberRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberEntity> findByName(final String name) {
        return Optional.ofNullable(
                queryFactory.selectFrom(memberEntity)
                        .where(nameEquals(name))
                        .fetchOne()
        );
    }

    @Override
    public Optional<MemberEntity> findByIdWithOptimisticLock(final Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(memberEntity)
                        .where(idEquals(id))
                        .setLockMode(LockModeType.OPTIMISTIC)
                        .fetchOne()
        );
    }

    @Override
    public Optional<MemberEntity> findByIdWithPessimisticLock(final Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(memberEntity)
                        .where(idEquals(id))
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .fetchOne()
        );
    }

    private BooleanExpression nameEquals(final String name) {
        return memberEntity.name.eq(name);
    }

    private BooleanExpression idEquals(final Long id) {
        return memberEntity.id.eq(id);
    }
}
