package org.jungppo.bambooforest.controller.member;

import static org.springframework.http.HttpStatus.*;

import org.jungppo.bambooforest.dto.member.JwtDto;
import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.response.exception.member.InvalidRefreshTokenException;
import org.jungppo.bambooforest.response.exception.member.RefreshTokenFailureException;
import org.jungppo.bambooforest.security.oauth2.CustomOAuth2User;
import org.jungppo.bambooforest.service.member.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
		memberService.logout(customOAuth2User.getId(), customOAuth2User.getOAuth2Type().getRegistrationId());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/profile")
	public ResponseEntity<MemberDto> getProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
		MemberDto memberDto = memberService.getProfile(customOAuth2User.getId());
		return ResponseEntity.ok().body(memberDto);
	}

	@PostMapping("/reissuance")
	public ResponseEntity<JwtDto> reissuanceToken(
		@RequestHeader(value = "Authorization") String refreshToken) {
		try {
			JwtDto jwtDto = memberService.reissuanceToken(refreshToken);
			return ResponseEntity.status(CREATED).body(jwtDto);
		} catch (InvalidRefreshTokenException e) {   // TODO. 함수형 인터페이스를 이용한 Refactoring
			throw new RefreshTokenFailureException();
		}
	}
}
