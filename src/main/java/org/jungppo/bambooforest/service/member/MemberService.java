package org.jungppo.bambooforest.service.member;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.repository.member.MemberRepository;
import org.jungppo.bambooforest.response.exception.member.MemberNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDto getProfile(Long memberId) {
        return memberRepository.findDtoById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
