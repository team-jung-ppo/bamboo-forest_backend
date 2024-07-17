package org.jungppo.bambooforest.chatbot.service;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chatbot.domain.ChatBotItem;
import org.jungppo.bambooforest.chatbot.dto.ChatBotPurchaseRequest;
import org.jungppo.bambooforest.chatbot.exception.ChatBotNotFoundException;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.jungppo.bambooforest.member.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatBotService {

    private final MemberRepository memberRepository;

    public synchronized void purchaseChatBot(final ChatBotPurchaseRequest chatBotPurchaseRequest,
                                             final CustomOAuth2User customOAuth2User) {
        final ChatBotItem chatBotItem = ChatBotItem.findByName(chatBotPurchaseRequest.getChatBotItemName())
                .orElseThrow(ChatBotNotFoundException::new);
        final MemberEntity memberEntity = memberRepository.findById(customOAuth2User.getId())
                .orElseThrow(MemberNotFoundException::new);

        memberEntity.subtractBatteries(chatBotItem.getPrice());
        memberEntity.addChatBot(chatBotItem);
        memberRepository.save(memberEntity);
    }
}
