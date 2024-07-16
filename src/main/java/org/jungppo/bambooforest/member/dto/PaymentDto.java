package org.jungppo.bambooforest.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.jungppo.bambooforest.payment.domain.entity.PaymentEntity;
import org.jungppo.bambooforest.payment.domain.entity.PaymentStatusType;

@Getter
public class PaymentDto {
    private final UUID id;
    private final PaymentStatusType status;
    private final String provider;
    private final BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @QueryProjection
    public PaymentDto(final UUID id, final PaymentStatusType status, final String provider, final BigDecimal amount,
                      final LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.provider = provider;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public static PaymentDto from(final PaymentEntity paymentEntity) {
        return new PaymentDto(
                paymentEntity.getId(),
                paymentEntity.getStatus(),
                paymentEntity.getProvider(),
                paymentEntity.getAmount(),
                paymentEntity.getCreatedAt()
        );
    }
}
