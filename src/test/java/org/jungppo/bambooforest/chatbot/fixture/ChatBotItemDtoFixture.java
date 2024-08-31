package org.jungppo.bambooforest.chatbot.fixture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;

public class ChatBotItemDtoFixture {

    public static ChatBotItemDto createChatBotItemDto(final ChatBotItem chatBotItem) {
        return ChatBotItemDto.from(chatBotItem);
    }

    public static List<ChatBotItemDto> createChatBotItemDtos() {
        return Stream.of(ChatBotItem.values())
                .map(ChatBotItemDtoFixture::createChatBotItemDto)
                .collect(Collectors.toList());
    }
}
