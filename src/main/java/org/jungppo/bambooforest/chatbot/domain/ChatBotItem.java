package org.jungppo.bambooforest.chatbot.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.exception.ChatBotNotAvailableException;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ChatBotItem {

    UNCLE_CHATBOT("아저씨 챗봇", "아저씨와 대화를 나눌 수 있는 챗봇입니다.",
            "https://bamboo-forest.s3.ap-northeast-2.amazonaws.com/Uncle.webp", 0, true),
    AUNT_CHATBOT("아줌마 챗봇", "아줌마와 대화를 나눌 수 있는 챗봇입니다.",
            "https://bamboo-forest.s3.ap-northeast-2.amazonaws.com/Aunt.webp", 3, true),
    CHILD_CHATBOT("어린이 챗봇", "어린이와 대화를 나눌 수 있는 챗봇입니다.",
            "https://bamboo-forest.s3.ap-northeast-2.amazonaws.com/Child.webp", 5, false);

    private final String name;
    private final String description;
    private final String imageUrl;
    private final int price;
    private final boolean available;

    private static final Map<String, ChatBotItem> CHATBOT_MAP;

    static {
        CHATBOT_MAP = Collections.unmodifiableMap(Arrays.stream(ChatBotItem.values())
                .collect(Collectors.toMap(ChatBotItem::getName, Function.identity())));
    }

    public static Optional<ChatBotItem> findByName(final String name) {
        return Optional.ofNullable(CHATBOT_MAP.get(name));
    }

    public void validateAvailability() {
        if (!this.available) {
            throw new ChatBotNotAvailableException();
        }
    }
}
