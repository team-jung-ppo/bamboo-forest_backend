package org.jungppo.bambooforest.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;

@Entity
@Getter
@Table(name = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageEntity extends JpaBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(nullable = false)
    private String content;

    private ChatMessageEntity(ChatRoomEntity chatRoom, MemberEntity member, String content) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.content = content;
    }
    
    public static ChatMessageEntity create(ChatRoomEntity chatRoom, MemberEntity member, String content) {
        return new ChatMessageEntity(chatRoom, member, content);
    }
}
