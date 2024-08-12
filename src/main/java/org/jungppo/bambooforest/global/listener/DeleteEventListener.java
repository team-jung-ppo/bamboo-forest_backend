package org.jungppo.bambooforest.global.listener;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.chat.domain.repository.ChatMessageRepository;
import org.jungppo.bambooforest.chat.domain.repository.ChatRoomRepository;
import org.jungppo.bambooforest.chatbot.domain.repository.ChatBotPurchaseRepository;
import org.jungppo.bambooforest.member.domain.MemberDeleteEvent;
import org.jungppo.bambooforest.payment.domain.repository.PaymentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteEventListener {

    private final PaymentRepository paymentRepository;
    private final ChatBotPurchaseRepository chatBotPurchaseRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @EventListener
    @Transactional(propagation = MANDATORY)
    public void deleteMember(final MemberDeleteEvent memberDeleteEvent) {
        chatMessageRepository.deleteAllByMemberId(memberDeleteEvent.getMemberId());
        chatRoomRepository.deleteAllByMemberId(memberDeleteEvent.getMemberId());
        chatBotPurchaseRepository.deleteAllByMemberId(memberDeleteEvent.getMemberId());
        paymentRepository.deleteAllByMemberId(memberDeleteEvent.getMemberId());
    }
}
