package org.jungppo.bambooforest.global.oauth2.service;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomJdbcOAuth2AuthorizedClientService extends JdbcOAuth2AuthorizedClientService {

    public CustomJdbcOAuth2AuthorizedClientService(JdbcOperations jdbcOperations,
                                                   ClientRegistrationRepository clientRegistrationRepository) {
        super(jdbcOperations, clientRegistrationRepository, new DefaultLobHandler());
    }
}
