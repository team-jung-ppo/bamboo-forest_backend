package org.jungppo.bambooforest.chatbot.dto;

import lombok.Getter;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;

@Getter
public class ChatBotItemDto {
    private final String name;
    private final String url;
    private final String description;
    private final String imageUrl;
    private final String prompt;

    public ChatBotItemDto(final ChatBotItem chatBotItem) {
        this.name = chatBotItem.getName();
        this.url = chatBotItem.getUrl();
        this.description = chatBotItem.getDescription();
        this.imageUrl = chatBotItem.getImageUrl();
        this.prompt = chatBotItem.getPrompt();
    }
}
