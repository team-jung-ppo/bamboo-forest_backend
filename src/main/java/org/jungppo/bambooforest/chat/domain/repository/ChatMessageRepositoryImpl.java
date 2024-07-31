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
    public Page<ChatMessageEntity> findLastMessagesByMemberId(Long roomId, Long memberId, Pageable pageable) {

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

        return new PageImpl<>(content, pageable, content.size());
    }

    private BooleanExpression memberIdEquals(Long memberId) {
        return chatMessageEntity.member.id.eq(memberId);
    }

    private BooleanExpression roomIdEquals(Long roomId) {
        return chatMessageEntity.chatRoom.id.eq(roomId);
    }
}
