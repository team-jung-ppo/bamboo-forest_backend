package org.jungppo.bambooforest.global.client.oauth2.github;

import static org.jungppo.bambooforest.member.domain.entity.OAuth2Type.OAUTH2_GITHUB;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.client.dto.ClientResponse;
import org.jungppo.bambooforest.global.client.oauth2.OAuth2Client;
import org.jungppo.bambooforest.global.client.oauth2.dto.UnlinkResponse;
import org.jungppo.bambooforest.global.client.oauth2.github.dto.GitHubUnlinkSuccessResponse;
import org.jungppo.bambooforest.global.client.oauth2.setting.OAuth2Properties;
import org.jungppo.bambooforest.global.oauth2.setting.GitHubConstants;
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
public final class GitHubOAuth2Client extends OAuth2Client {

    private final RestTemplate restTemplate;
    private final OAuth2Properties oauth2Properties;

    @Override
    protected String getSupportedProvider() {
        return OAUTH2_GITHUB.name();
    }

    @Override
    public ClientResponse<UnlinkResponse> unlink(final String identifier) {
        try {
            final HttpEntity<String> request = createUnlinkRequest(identifier);
            final ResponseEntity<String> response = restTemplate.exchange(
                    GitHubConstants.GITHUB_URL + oauth2Properties.getGithub().getClientId()
                            + GitHubConstants.GITHUB_UNLINK_URL,
                    HttpMethod.DELETE, request, String.class
            );
            handleResponse(response);
            return ClientResponse.success(createUnlinkResponse());
        } catch (final Exception e) {
            log.warn("Failed to unlink GitHub account.", e);
            return ClientResponse.failure();
        }
    }

    private HttpEntity<String> createUnlinkRequest(final String accessToken) {
        final HttpHeaders headers = getHeaders();
        final String body = String.format("{\"access_token\":\"%s\"}", accessToken);
        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders getHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(oauth2Properties.getGithub().getClientId(),
                oauth2Properties.getGithub().getClientSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void handleResponse(final ResponseEntity<?> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to process GitHub request.");
        }
    }

    private GitHubUnlinkSuccessResponse createUnlinkResponse() {
        return new GitHubUnlinkSuccessResponse("Successfully unlinked GitHub account.");
    }
}
