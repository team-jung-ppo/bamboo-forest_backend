package org.jungppo.bambooforest.chatbot.fixture;

import static org.jungppo.bambooforest.util.ReflectionUtils.setField;

import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.jungppo.bambooforest.member.fixture.MemberEntityFixture;

public class ChatBotPurchaseEntityFixture {

    public static final ChatBotPurchaseEntity UNCLE_PURCHASE_ENTITY;
    public static final ChatBotPurchaseEntity AUNT_PURCHASE_ENTITY;

    static {
        UNCLE_PURCHASE_ENTITY = ChatBotPurchaseEntity.of(
                ChatBotItem.UNCLE_CHATBOT.getPrice(),
                ChatBotItem.UNCLE_CHATBOT,
                MemberEntityFixture.MEMBER_ENTITY
        );
        setField(UNCLE_PURCHASE_ENTITY, "id", 1L);

        AUNT_PURCHASE_ENTITY = ChatBotPurchaseEntity.of(
                ChatBotItem.AUNT_CHATBOT.getPrice(),
                ChatBotItem.AUNT_CHATBOT,
                MemberEntityFixture.MEMBER_ENTITY
        );
        setField(AUNT_PURCHASE_ENTITY, "id", 2L);
    }
}
