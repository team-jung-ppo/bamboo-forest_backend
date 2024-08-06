package org.jungppo.bambooforest.global.client.oauth2.kakao;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_KAKAO;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.client.oauth2.OAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.setting.OAuth2Properties;
import org.jungppo.bambooforest.global.oauth2.setting.KakaoConstants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(OAuth2Properties.class)
public final class KakaoOAuth2Client extends OAuth2Client {

    private final RestTemplate restTemplate;
    private final OAuth2Properties oauth2Properties;

    @Override
    protected String getSupportedProvider() {
        return OAUTH2_KAKAO.name();
    }

    @Override
    public void unlink(final String memberId) {
        try {
            final HttpEntity<String> request = createUnlinkRequest(memberId);
            final ResponseEntity<String> response = restTemplate.exchange(
                    KakaoConstants.KAKAO_UNLINK_URL, HttpMethod.POST,
                    request, String.class
            );
            handleResponse(response);
        } catch (final Exception e) {
            log.error("Failed to unlink Kakao account.", e);
        }
    }

    private HttpEntity<String> createUnlinkRequest(final String providerMemberId) {
        final HttpHeaders headers = getHeaders();
        final String body = String.format(KakaoConstants.KAKAO_UNLINK_REQUEST_BODY_FORMAT, providerMemberId);
        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders getHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, oauth2Properties.getKakao().getAdminKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private void handleResponse(final ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to unlink Kakao account.");
        }
    }
}
