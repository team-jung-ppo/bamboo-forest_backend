package org.jungppo.bambooforest.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements QuerydslMemberRepository {
    private final JPAQueryFactory queryFactory;
}
