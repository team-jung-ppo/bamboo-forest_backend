package org.jungppo.bambooforest.global.jpa.setting;

import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;

@Converter
public class ChatBotItemEnumSetConverter implements AttributeConverter<EnumSet<ChatBotItem>, String> {

    @Override
    public String convertToDatabaseColumn(final EnumSet<ChatBotItem> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final ChatBotItem chatBotItem : attribute) {
            sb.append(chatBotItem.name()).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public EnumSet<ChatBotItem> convertToEntityAttribute(final String dbData) {
        if (!hasText(dbData)) {
            return EnumSet.noneOf(ChatBotItem.class);
        }
        final Set<String> itemNames = Arrays.stream(dbData.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        final EnumSet<ChatBotItem> enumSet = EnumSet.noneOf(ChatBotItem.class);
        for (final String itemName : itemNames) {
            enumSet.add(ChatBotItem.valueOf(itemName));
        }
        return enumSet;
    }
}
