package org.jungppo.bambooforest.chat.domain.repository;

import static org.jungppo.bambooforest.chat.domain.entity.QChatRoomEntity.chatRoomEntity;

import org.jungppo.bambooforest.chat.domain.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements QuerydslChatRoomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChatRoomEntity> findChatRoomsByMemberId(Long memberId, Pageable pageable) {
         List<ChatRoomEntity> content = queryFactory
                .selectFrom(chatRoomEntity)
                .where(chatRoomEntity.member.id.eq(memberId))
                .orderBy(chatRoomEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        long total = queryFactory
                .select(chatRoomEntity.count())
                .from(chatRoomEntity)
                .where(chatRoomEntity.member.id.eq(memberId))
                .fetchOne();
        
        return new PageImpl<>(content, pageable, total);
    }
}
