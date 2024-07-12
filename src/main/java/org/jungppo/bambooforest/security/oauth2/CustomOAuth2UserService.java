package org.jungppo.bambooforest.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.security.oauth2.response.OAuth2Response;
import org.jungppo.bambooforest.security.oauth2.response.OAuth2ResponseFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.jungppo.bambooforest.entity.type.RoleType.ROLE_USER;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = OAuth2ResponseFactory.getOAuth2Response(registrationId, oAuth2User.getAttributes());
        String name = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        return new CustomOAuth2User(1L, name, ROLE_USER);  // TODO. 회원 엔티티 구현 후 수정
    }
}
