package org.jungppo.bambooforest.payment.domain.repository;

import static org.jungppo.bambooforest.payment.domain.entity.QPaymentEntity.paymentEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.jungppo.bambooforest.payment.domain.entity.PaymentStatusType;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements QuerydslPaymentRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PaymentEntity> findAllCompletedByMemberIdOrderByCreatedAtDesc(Long memberId) {
        return queryFactory.selectFrom(paymentEntity)
                .where(memberIdEquals(memberId)
                        .and(statusIsCompleted()))
                .orderBy(paymentEntity.createdAt.desc())
                .fetch();
    }

    private BooleanExpression memberIdEquals(Long memberId) {
        return paymentEntity.member.id.eq(memberId);
    }

    private BooleanExpression statusIsCompleted() {
        return paymentEntity.status.eq(PaymentStatusType.COMPLETED);
    }
}
