package org.jungppo.bambooforest.entity.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.jungppo.bambooforest.entity.battery.BatteryItem;
import org.jungppo.bambooforest.entity.member.MemberEntity;

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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class PaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "payment_id")
	private UUID id;

	@Column(name = "pay_method", nullable = false)
	private String payMethod;

	@Column(name = "key", nullable = false)
	private String key;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "battery_item", nullable = false)
	private BatteryItem batteryItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private MemberEntity member;

	@Builder
	public PaymentEntity(String payMethod, String key, BigDecimal amount, BatteryItem batteryItem,
		MemberEntity member) {
		this.payMethod = payMethod;
		this.key = key;
		this.amount = amount;
		this.batteryItem = batteryItem;
		this.member = member;
	}
}
