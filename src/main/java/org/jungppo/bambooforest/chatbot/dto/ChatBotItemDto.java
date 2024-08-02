package org.jungppo.bambooforest.chatbot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;

@Getter
@RequiredArgsConstructor
public class ChatBotItemDto {
    private final String name;
    private final String description;
    private final String imageUrl;
    private final int price;
    private final boolean available;

    public static ChatBotItemDto from(final ChatBotItem chatBotItem) {
        return new ChatBotItemDto(
                chatBotItem.getName(),
                chatBotItem.getDescription(),
                chatBotItem.getImageUrl(),
                chatBotItem.getPrice(),
                chatBotItem.isAvailable()
        );
    }
}
