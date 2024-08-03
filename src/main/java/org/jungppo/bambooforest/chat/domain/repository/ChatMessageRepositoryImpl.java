package org.jungppo.bambooforest.chat.domain.repository;

import static org.jungppo.bambooforest.chat.domain.entity.QChatMessageEntity.chatMessageEntity;

import java.util.List;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements QuerydslChatMessageRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChatMessageEntity> findPagedMessagesByMemberId(Long roomId, Long memberId, Pageable pageable) {

        List<ChatMessageEntity> content = queryFactory
            .selectFrom(chatMessageEntity)
            .where(
                memberIdEquals(memberId),
                roomIdEquals(roomId)
            )
            .orderBy(chatMessageEntity.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
                .select(chatMessageEntity.count())
                .from(chatMessageEntity)
                .where(
                        memberIdEquals(memberId),
                        roomIdEquals(roomId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void deleteAllByChatRoomId(Long chatRoomId) {
        queryFactory
            .delete(chatMessageEntity)
            .where(roomIdEquals(chatRoomId))
            .execute();
    }

    private BooleanExpression memberIdEquals(Long memberId) {
        return chatMessageEntity.member.id.eq(memberId);
    }

    private BooleanExpression roomIdEquals(Long roomId) {
        return chatMessageEntity.chatRoom.id.eq(roomId);
    }
}
