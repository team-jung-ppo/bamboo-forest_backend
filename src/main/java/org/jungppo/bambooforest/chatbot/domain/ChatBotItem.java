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

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ChatBotItem {

    UNCLE_CHATBOT("아저씨 챗봇", "http://example.com/uncle", "아저씨와 대화를 나눌 수 있는 챗봇입니다.",
            "http://example.com/images/uncle.png", "안녕하세요, 아저씨 챗봇입니다. 무엇을 도와드릴까요?"),
    AUNT_CHATBOT("아줌마 챗봇", "http://example.com/aunt", "아줌마와 대화를 나눌 수 있는 챗봇입니다.",
            "http://example.com/images/aunt.png", "안녕하세요, 아줌마 챗봇입니다. 무엇을 도와드릴까요?"),
    CHILD_CHATBOT("어린이 챗봇", "http://example.com/child", "어린이와 대화를 나눌 수 있는 챗봇입니다.",
            "http://example.com/images/child.png", "안녕하세요, 어린이 챗봇입니다. 무엇을 도와드릴까요?");

    private final String name;
    private final String url;
    private final String description;
    private final String imageUrl;
    private final String prompt;

    private static final Map<String, ChatBotItem> CHATBOT_MAP;

    static {
        CHATBOT_MAP = Collections.unmodifiableMap(Arrays.stream(ChatBotItem.values())
                .collect(Collectors.toMap(ChatBotItem::getName, Function.identity())));
    }

    public static Optional<ChatBotItem> findByName(final String name) {
        return Optional.ofNullable(CHATBOT_MAP.get(name));
    }
}
