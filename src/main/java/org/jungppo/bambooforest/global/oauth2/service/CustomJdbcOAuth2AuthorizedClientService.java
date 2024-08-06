package org.jungppo.bambooforest.global.oauth2.service;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.transaction.annotation.Transactional;

public class CustomJdbcOAuth2AuthorizedClientService extends JdbcOAuth2AuthorizedClientService {

    public CustomJdbcOAuth2AuthorizedClientService(JdbcOperations jdbcOperations,
                                                   ClientRegistrationRepository clientRegistrationRepository) {
        super(jdbcOperations, clientRegistrationRepository, new DefaultLobHandler());
    }

    @Override
    @Transactional
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
                                                                     String principalName) {
        return super.loadAuthorizedClient(clientRegistrationId, principalName);
    }

    @Override
    @Transactional
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        super.saveAuthorizedClient(authorizedClient, principal);
    }

    @Override
    @Transactional
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        super.removeAuthorizedClient(clientRegistrationId, principalName);
    }
}
