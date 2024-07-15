package org.jungppo.bambooforest.global.oauth2.dto;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;

import java.util.Map;
import org.jungppo.bambooforest.global.oauth2.setting.KakaoConstants;
import org.jungppo.bambooforest.member.domain.entity.OAuth2Type;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;
    private final Map<String, Object> profileAttributes;

    public KakaoResponse(final Map<String, Object> attribute) {
        this.attribute = attribute;
        this.profileAttributes = extractProfileAttributes(attribute);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractProfileAttributes(final Map<String, Object> attribute) {
        if (attribute.get(KakaoConstants.KAKAO_ACCOUNT) instanceof Map<?, ?>) {
            final Map<String, Object> kakaoAccountAttributes = (Map<String, Object>) attribute.get(
                    KakaoConstants.KAKAO_ACCOUNT);
            if (kakaoAccountAttributes.get(KakaoConstants.KAKAO_PROFILE) instanceof Map<?, ?>) {
                return (Map<String, Object>) kakaoAccountAttributes.get(KakaoConstants.KAKAO_PROFILE);
            }
        }
        throw new IllegalArgumentException("Profile attributes could not be extracted.");
    }

    @Override
    public OAuth2Type getProvider() {
        return OAUTH2_KAKAO;
    }

    @Override
    public String getProviderId() {
        return attribute.get(KakaoConstants.KAKAO_ID).toString();
    }

    @Override
    public String getName() {
        return profileAttributes.get(KakaoConstants.KAKAO_NICKNAME).toString();
    }

    @Override
    public String getProfileImage() {
        return profileAttributes.get(KakaoConstants.KAKAO_PROFILE_IMAGE_URL).toString();
    }
}
