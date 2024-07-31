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
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(nullable = false)
    private String userMessage;

    @Column(nullable = false)
    private String botMessage;

    @Column(name = "chatbot_name", nullable = false)
    private String chatbotName;

    private ChatMessageEntity(ChatRoomEntity chatRoom, MemberEntity member, String userMessage, String botMessage, String chatbotName) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.userMessage = userMessage;
        this.botMessage = botMessage;
        this.chatbotName = chatbotName;
    }
    
    public static ChatMessageEntity createMessage(ChatRoomEntity chatRoom, MemberEntity member, String userMessage, String botMessage, String chatbotName) {
        return new ChatMessageEntity(chatRoom, member, userMessage, botMessage, chatbotName);
    }
}
