package org.jungppo.bambooforest.chatbot.fixture;

import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.domain.entity.ChatBotPurchaseEntity;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.util.ReflectionUtils;

public class ChatBotPurchaseEntityFixture {

    public static ChatBotPurchaseEntity createChatBotPurchaseEntity(final Long id, final int amount,
                                                                    final ChatBotItem chatBotItem,
                                                                    final MemberEntity member) {
        final ChatBotPurchaseEntity purchaseEntity = ChatBotPurchaseEntity.of(amount, chatBotItem, member);
        ReflectionUtils.setField(purchaseEntity, "id", id);
        return purchaseEntity;
    }
}
