package org.jungppo.bambooforest.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEntity extends JpaBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String roomId;

    private ChatRoomEntity(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public static ChatRoomEntity create(String roomId, String name) {
        return new ChatRoomEntity(roomId, name);
    }
}
