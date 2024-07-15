package org.jungppo.bambooforest.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class PaymentEntity extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusType status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatteryItem batteryItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "`key`", unique = true)
    private String key;

    private String provider;

    private BigDecimal amount;

    @Builder
    public PaymentEntity(@NonNull final PaymentStatusType status, @NonNull final BatteryItem batteryItem,
                         @NonNull final MemberEntity member) {
        this.status = status;
        this.batteryItem = batteryItem;
        this.member = member;
    }

    public void updatePaymentStatus(final PaymentStatusType status) {
        this.status = status;
    }

    public void updatePaymentDetails(final String key, final String provider, final BigDecimal amount) {
        this.key = key;
        this.provider = provider;
        this.amount = amount;
    }
}
