package org.jungppo.bambooforest.security.oauth2;

import static org.jungppo.bambooforest.entity.type.RoleType.*;

import org.jungppo.bambooforest.entity.member.MemberEntity;
import org.jungppo.bambooforest.repository.member.MemberRepository;
import org.jungppo.bambooforest.security.oauth2.response.OAuth2Response;
import org.jungppo.bambooforest.security.oauth2.response.OAuth2ResponseFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = OAuth2ResponseFactory.getOAuth2Response(registrationId,
			oAuth2User.getAttributes());
		String name = oAuth2Response.getProvider().name() + "_" + oAuth2Response.getProviderId();
		MemberEntity memberEntity = memberRepository.findByName(name)
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
