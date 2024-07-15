package org.jungppo.bambooforest.entity.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.jungppo.bambooforest.entity.battery.BatteryItem;
import org.jungppo.bambooforest.entity.common.JpaBaseEntity;
import org.jungppo.bambooforest.entity.type.PaymentStatusType;
import org.jungppo.bambooforest.member.domain.MemberEntity;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
	public PaymentEntity(@NonNull PaymentStatusType status, @NonNull BatteryItem batteryItem,
		@NonNull MemberEntity member) {
		this.status = status;
		this.batteryItem = batteryItem;
		this.member = member;
	}

	public void updatePaymentStatus(PaymentStatusType status) {
		this.status = status;
	}

	public void updatePaymentDetails(String key, String provider, BigDecimal amount) {
		this.key = key;
		this.provider = provider;
		this.amount = amount;
	}
}
