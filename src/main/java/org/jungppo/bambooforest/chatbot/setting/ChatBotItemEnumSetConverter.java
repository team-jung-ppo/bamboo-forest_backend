package org.jungppo.bambooforest.chatbot.setting;

import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;

@Converter
public class ChatBotItemEnumSetConverter implements AttributeConverter<EnumSet<ChatBotItem>, String> {

    @Override
    public String convertToDatabaseColumn(final EnumSet<ChatBotItem> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }

        return attribute.stream()
                .map(ChatBotItem::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public EnumSet<ChatBotItem> convertToEntityAttribute(final String dbData) {
        if (!hasText(dbData)) {
            return EnumSet.noneOf(ChatBotItem.class);
        }

        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .map(ChatBotItem::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ChatBotItem.class)));
    }
}
