package org.jungppo.bambooforest.chat.domain.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEntity extends JpaBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatBotItem chatBotItem;

    private ChatRoomEntity(String roomId, MemberEntity member, ChatBotItem chatBotItem) {
        this.roomId = roomId;
        this.member = member;
        this.chatBotItem = chatBotItem;
    }

    public static ChatRoomEntity of(String roomId, MemberEntity member, ChatBotItem chatBotItem) {
        return new ChatRoomEntity(roomId, member, chatBotItem);
    }
}
