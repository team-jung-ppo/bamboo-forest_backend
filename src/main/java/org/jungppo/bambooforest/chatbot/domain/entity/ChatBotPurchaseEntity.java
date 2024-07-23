package org.jungppo.bambooforest.chatbot.domain.entity;

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
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatbot_purchase")
public class ChatBotPurchaseEntity extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatbot_purchase_id")
    private Long id;

    @Column(nullable = false)
    private int amount;  // 챗봇 금액과 결제 금액이 다를 수 있음.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatBotItem chatBotItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Builder
    public ChatBotPurchaseEntity(final int amount, @NonNull final ChatBotItem chatBotItem,
                                 @NonNull final MemberEntity member) {
        this.amount = amount;
        this.chatBotItem = chatBotItem;
        this.member = member;
    }
}
