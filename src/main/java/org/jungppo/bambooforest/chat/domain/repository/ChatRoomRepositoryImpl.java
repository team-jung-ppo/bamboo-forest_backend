package org.jungppo.bambooforest.chat.domain.repository;

import static org.jungppo.bambooforest.chat.domain.entity.QChatRoomEntity.chatRoomEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements QuerydslChatRoomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChatRoomEntity> findChatRoomsByMemberId(Long memberId, Pageable pageable) {
        List<ChatRoomEntity> content = queryFactory
                .selectFrom(chatRoomEntity)
                .where(memberIdEquals(memberId))
                .orderBy(chatRoomEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(chatRoomEntity.count())
                .from(chatRoomEntity)
                .where(memberIdEquals(memberId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void deleteAllByMemberId(Long memberId) {
        queryFactory.delete(chatRoomEntity)
                .where(memberIdEquals(memberId))
                .execute();
    }

    private BooleanExpression memberIdEquals(Long memberId) {
        return chatRoomEntity.member.id.eq(memberId);
    }
}
