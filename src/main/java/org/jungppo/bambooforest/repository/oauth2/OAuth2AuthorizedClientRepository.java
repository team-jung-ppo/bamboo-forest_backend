package org.jungppo.bambooforest.repository.oauth2;

import org.jungppo.bambooforest.entity.oauth2.OAuth2AuthorizedClient;
import org.jungppo.bambooforest.entity.oauth2.OAuth2AuthorizedClientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2AuthorizedClientRepository extends JpaRepository<OAuth2AuthorizedClient, OAuth2AuthorizedClientId> {

}
