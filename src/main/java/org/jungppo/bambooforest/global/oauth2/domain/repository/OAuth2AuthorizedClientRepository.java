package org.jungppo.bambooforest.global.oauth2.domain.repository;

import org.jungppo.bambooforest.global.oauth2.domain.entity.OAuth2AuthorizedClientEntity;
import org.jungppo.bambooforest.global.oauth2.domain.entity.OAuth2AuthorizedClientEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2AuthorizedClientRepository extends
        JpaRepository<OAuth2AuthorizedClientEntity, OAuth2AuthorizedClientEntityId> {

}
