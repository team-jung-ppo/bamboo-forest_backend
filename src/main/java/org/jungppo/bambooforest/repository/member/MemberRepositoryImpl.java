package org.jungppo.bambooforest.repository.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.entity.member.MemberEntity;

import java.util.Optional;

import static org.jungppo.bambooforest.entity.member.QMemberEntity.memberEntity;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements QuerydslMemberRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberEntity> findByName(String name) {
        return Optional.ofNullable(
                queryFactory.selectFrom(memberEntity)
                        .where(nameEquals(name))
                        .fetchOne()
        );
    }

    private BooleanExpression nameEquals(String name) {
        return memberEntity.name.eq(name);
    }
}
