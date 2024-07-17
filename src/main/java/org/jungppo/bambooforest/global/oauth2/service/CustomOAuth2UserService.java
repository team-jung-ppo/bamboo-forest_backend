package org.jungppo.bambooforest.global.oauth2.service;

import static org.jungppo.bambooforest.member.domain.entity.RoleType.ROLE_USER;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.global.oauth2.domain.CustomOAuth2User;
import org.jungppo.bambooforest.global.oauth2.dto.OAuth2Response;
import org.jungppo.bambooforest.member.domain.entity.MemberEntity;
import org.jungppo.bambooforest.member.domain.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) {
        final OAuth2User oAuth2User = super.loadUser(userRequest);
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();
        final OAuth2Response oAuth2Response = OAuth2ResponseFactory.getOAuth2Response(registrationId,
                oAuth2User.getAttributes());
        final String name = oAuth2Response.getProvider().name() + "_" + oAuth2Response.getProviderId();
        final MemberEntity memberEntity = memberRepository.findByName(name)
                .map(existingMember -> {
                    existingMember.updateInfo(oAuth2Response.getName(), oAuth2Response.getProfileImage());
                    return existingMember;
                })
                .orElseGet(() ->
                        memberRepository.save(
                                MemberEntity.builder()
                                        .name(name)
                                        .oAuth2(oAuth2Response.getProvider())
                                        .username(oAuth2Response.getName())
                                        .profileImage(oAuth2Response.getProfileImage())
                                        .role(ROLE_USER)
                                        .build()
                        ));

        return new CustomOAuth2User(memberEntity.getId(), memberEntity.getRole(), memberEntity.getOAuth2());
    }
}
