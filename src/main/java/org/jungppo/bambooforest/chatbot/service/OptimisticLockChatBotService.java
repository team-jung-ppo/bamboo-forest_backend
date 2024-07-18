package org.jungppo.bambooforest.chatbot.service;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OptimisticLockChatBotService {

    private static final int MAX_RETRIES = 3;

    private final ChatBotService chatBotService;

    public void optimisticLockPurchaseChatBot(final ChatBotPurchaseRequest chatBotPurchaseRequest,
                                              final CustomOAuth2User customOAuth2User) throws InterruptedException {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                chatBotService.purchaseChatBot(chatBotPurchaseRequest, customOAuth2User);
            } catch (OptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw e;
                }
                Thread.sleep(50);
            }
        }
    }
}
