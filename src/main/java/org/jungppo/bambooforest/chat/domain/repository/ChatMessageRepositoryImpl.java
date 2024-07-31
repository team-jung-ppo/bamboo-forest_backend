package org.jungppo.bambooforest.chat.domain.repository;

import static org.jungppo.bambooforest.chat.domain.entity.QChatMessageEntity.chatMessageEntity;

import java.util.List;

import org.jungppo.bambooforest.chat.domain.entity.ChatMessageEntity;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements QuerydslChatMessageRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatMessageEntity> findLastMessagesByMemberId(String roomId, Long memberId, Pageable pageable) {
        return queryFactory
            .selectFrom(chatMessageEntity)
            .where(
                chatMessageEntity.member.id.eq(memberId),
                chatMessageEntity.chatRoom.roomId.eq(roomId)
            )
            .orderBy(chatMessageEntity.createdAt.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
