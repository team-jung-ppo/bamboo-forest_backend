package org.jungppo.bambooforest.chat.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_bot")
@NoArgsConstructor
public class ChatBotEntity extends JpaBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String prompt;

    @Builder
    public ChatBotEntity(String url, String name, String description, String imageUrl, String prompt) {
        this.url = url;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.prompt = prompt;
    }
}
