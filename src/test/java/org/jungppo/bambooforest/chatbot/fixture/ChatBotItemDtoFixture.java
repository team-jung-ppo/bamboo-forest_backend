package org.jungppo.bambooforest.chatbot.fixture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.dto.ChatBotItemDto;

public class ChatBotItemDtoFixture {

    public static final ChatBotItemDto UNCLE_CHATBOT_DTO = ChatBotItemDto.from(ChatBotItem.UNCLE_CHATBOT);

    public static final ChatBotItemDto AUNT_CHATBOT_DTO = ChatBotItemDto.from(ChatBotItem.AUNT_CHATBOT);

    public static final ChatBotItemDto CHILD_CHATBOT_DTO = ChatBotItemDto.from(ChatBotItem.CHILD_CHATBOT);

    public static final List<ChatBotItemDto> CHATBOT_ITEM_DTOS = Stream.of(ChatBotItem.values())
            .map(ChatBotItemDto::from)
            .collect(Collectors.toList());
}
