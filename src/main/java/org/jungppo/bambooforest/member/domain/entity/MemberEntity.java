package org.jungppo.bambooforest.member.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.EnumSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.jungppo.bambooforest.battery.exception.BatteryInsufficientException;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.exception.ChatBotAlreadyOwnedException;
import org.jungppo.bambooforest.chatbot.setting.ChatBotItemEnumSetConverter;
import org.jungppo.bambooforest.global.jpa.domain.entity.JpaBaseEntity;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuth2Type oAuth2;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private int batteryCount = 0;

    /**
     * 트랜잭션 내부에서는 단일 스레드 환경에서 동작함. synchronizedSet 사용 필요 없음
     */
    @Convert(converter = ChatBotItemEnumSetConverter.class)
    @Column(name = "chat_bots", nullable = false)
    private final EnumSet<ChatBotItem> chatBots = EnumSet.noneOf(ChatBotItem.class);

    @Version
    private Long version;

    @Builder
    public MemberEntity(@NonNull final String name, @NonNull final OAuth2Type oAuth2, @NonNull final String username,
                        @NonNull final String profileImage, @NonNull final RoleType role) {
        this.name = name;
        this.oAuth2 = oAuth2;
        this.username = username;
        this.profileImage = profileImage;
        this.role = role;
    }

    public void updateInfo(final String username, final String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public void addBatteries(final int count) {
        this.batteryCount += count;
    }

    public void subtractBatteries(final int count) {
        if (this.batteryCount < count) {
            throw new BatteryInsufficientException();
        }
        this.batteryCount -= count;
    }

    public void addChatBot(final ChatBotItem chatBotItem) {
        if (this.chatBots.contains(chatBotItem)) {
            throw new ChatBotAlreadyOwnedException();
        }
        this.chatBots.add(chatBotItem);
    }

    public void removeChatBot(final ChatBotItem chatBotItem) {
        this.chatBots.remove(chatBotItem);
    }

    public boolean hasPurchasedChatBot(final ChatBotItem chatBotItem) {
        return chatBots.contains(chatBotItem);
    }
}
