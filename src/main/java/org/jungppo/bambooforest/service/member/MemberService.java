package org.jungppo.bambooforest.service.member;

import static org.jungppo.bambooforest.config.JwtConfig.*;

import org.jungppo.bambooforest.dto.member.JwtDto;
import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.entity.member.RefreshTokenEntity;
import org.jungppo.bambooforest.entity.oauth2.OAuth2AuthorizedClientEntityId;
import org.jungppo.bambooforest.entity.type.OAuth2Type;
import org.jungppo.bambooforest.entity.type.RoleType;
import org.jungppo.bambooforest.repository.member.MemberRepository;
import org.jungppo.bambooforest.repository.oauth2.OAuth2AuthorizedClientRepository;
import org.jungppo.bambooforest.response.exception.member.InvalidRefreshTokenException;
import org.jungppo.bambooforest.response.exception.member.MemberNotFoundException;
import org.jungppo.bambooforest.response.exception.member.RefreshTokenFailureException;
import org.jungppo.bambooforest.security.jwt.JwtMemberClaim;
import org.jungppo.bambooforest.security.oauth2.CustomOAuth2User;
import org.jungppo.bambooforest.util.JwtUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
	private final RefreshTokenService refreshTokenService;  // 같은 도메인이기 때문에 의존 가능
	private final JwtUtils jwtAccessTokenUtils;
	private final JwtUtils jwtRefreshTokenUtils;

	public MemberService(MemberRepository memberRepository,
		OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, RefreshTokenService refreshTokenService,
		@Qualifier(JWT_ACCESS_TOKEN_UTILS) JwtUtils jwtAccessTokenUtils,
		@Qualifier(JWT_REFRESH_TOKEN_UTILS) JwtUtils jwtRefreshTokenUtils) {
		this.memberRepository = memberRepository;
		this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
		this.refreshTokenService = refreshTokenService;
		this.jwtAccessTokenUtils = jwtAccessTokenUtils;
		this.jwtRefreshTokenUtils = jwtRefreshTokenUtils;
	}

	@Transactional
	public void logout(CustomOAuth2User customOAuth2User) {
		refreshTokenService.deleteById(customOAuth2User.getId());  // 내부적으로 ifPresent 일때만 삭제하도록 처리되어있음.
		oAuth2AuthorizedClientRepository.deleteById(
			new OAuth2AuthorizedClientEntityId(customOAuth2User.getOAuth2Type().getRegistrationId(),
				customOAuth2User.getId().toString()));  // OAuth2 Server(Kakao, GitHub)에게 발급받은 정보들도 삭제
	}

	public MemberDto getProfile(CustomOAuth2User customOAuth2User) {
		return memberRepository.findDtoById(customOAuth2User.getId()).orElseThrow(MemberNotFoundException::new);
	}

	@Transactional
	public JwtDto reissuanceToken(String refreshToken) throws
		InvalidRefreshTokenException { // 협업의 원활함을 위하여 Header, Cookie가 아닌 Body로 반환
		JwtMemberClaim jwtMemberClaim = jwtRefreshTokenUtils.parseOptionalToken(refreshToken)
			.orElseThrow(RefreshTokenFailureException::new);
		Long id = jwtMemberClaim.getId();
		RoleType roleType = jwtMemberClaim.getRoleType();
		OAuth2Type oAuth2Type = jwtMemberClaim.getOAuth2Type();

		validateRefreshToken(id, refreshToken);
		String newAccessToken = jwtAccessTokenUtils.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));
		String newRefreshToken = jwtRefreshTokenUtils.createToken(new JwtMemberClaim(id, roleType, oAuth2Type));

		refreshTokenService.saveOrUpdateRefreshToken(id, newRefreshToken);
		return new JwtDto(newAccessToken, newRefreshToken);
	}

	private void validateRefreshToken(Long id, String refreshToken) throws
		InvalidRefreshTokenException {  //  CheckedException을 통해 트랜잭션 발생하지 않으면서 오류 메세지 출력
		RefreshTokenEntity existingToken = refreshTokenService.findById(id)
			.orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found."));
		if (!existingToken.getValue().equals(refreshToken)) {
			refreshTokenService.deleteById(id);
			throw new InvalidRefreshTokenException("Invalid refresh token.");
		}
	}
}
