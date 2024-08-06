package org.jungppo.bambooforest.chatbot.domain.repository;

import static org.jungppo.bambooforest.chatbot.domain.entity.QChatBotPurchaseEntity.chatBotPurchaseEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;

@RequiredArgsConstructor
public class ChatBotPurchaseRepositoryImpl implements QuerydslChatBotPurchaseRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatBotPurchaseEntity> findAllByMemberIdOrderByCreatedAtDesc(Long memberId) {
        return queryFactory.selectFrom(chatBotPurchaseEntity)
                .where(memberIdEquals(memberId))
                .orderBy(chatBotPurchaseEntity.createdAt.desc())
                .fetch();
    }

    @Override
    public void deleteAllByMemberId(Long memberId) {
        queryFactory.delete(chatBotPurchaseEntity)
                .where(memberIdEquals(memberId))
                .execute();
    }

    private BooleanExpression memberIdEquals(Long memberId) {
        return chatBotPurchaseEntity.member.id.eq(memberId);
    }
}
