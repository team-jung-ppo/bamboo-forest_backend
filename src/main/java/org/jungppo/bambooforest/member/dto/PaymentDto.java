package org.jungppo.bambooforest.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jungppo.bambooforest.payment.domain.PaymentStatusType;

@Data
@NoArgsConstructor
public class PaymentDto {
    private UUID id;
    private PaymentStatusType status;
    private String provider;
    private BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public PaymentDto(UUID id, PaymentStatusType status, String provider, BigDecimal amount,
                      LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.provider = provider;
        this.amount = amount;
        this.createdAt = createdAt;
    }
}
