package org.jungppo.bambooforest.repository.member;

import static org.jungppo.bambooforest.entity.member.QMemberEntity.*;

import java.util.Optional;

import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.dto.member.QMemberDto;
import org.jungppo.bambooforest.entity.member.MemberEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

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

	@Override
	public Optional<MemberDto> findDtoById(Long id) {
		return Optional.ofNullable(
			queryFactory.select(new QMemberDto(
					memberEntity.id,
					memberEntity.oAuth2,
					memberEntity.username,
					memberEntity.profileImage,
					memberEntity.role,
					memberEntity.batteryCount,
					memberEntity.createdAt
				))
				.from(memberEntity)
				.where(idEquals(id))
				.fetchOne()
		);
	}

	private BooleanExpression nameEquals(String name) {
		return memberEntity.name.eq(name);
	}

	private BooleanExpression idEquals(Long id) {
		return memberEntity.id.eq(id);
	}
}
