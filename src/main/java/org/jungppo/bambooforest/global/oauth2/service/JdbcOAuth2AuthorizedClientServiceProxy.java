package org.jungppo.bambooforest.global.oauth2.service;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.transaction.annotation.Transactional;

public class JdbcOAuth2AuthorizedClientServiceProxy implements OAuth2AuthorizedClientService {

    private final OAuth2AuthorizedClientService delegate;

    public JdbcOAuth2AuthorizedClientServiceProxy(JdbcOperations jdbcOperations,
                                                  ClientRegistrationRepository clientRegistrationRepository) {
        this.delegate = new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
    }

    @Override
    @Transactional
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
                                                                     String principalName) {
        return delegate.loadAuthorizedClient(clientRegistrationId, principalName);
    }

    @Override
    @Transactional
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        delegate.saveAuthorizedClient(authorizedClient, principal);
    }

    @Override
    @Transactional
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        delegate.removeAuthorizedClient(clientRegistrationId, principalName);
    }
}
