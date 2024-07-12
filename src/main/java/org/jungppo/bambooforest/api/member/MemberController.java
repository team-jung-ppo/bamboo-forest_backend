package org.jungppo.bambooforest.api.member;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.dto.member.JwtDto;
import org.jungppo.bambooforest.dto.member.MemberDto;
import org.jungppo.bambooforest.response.ResponseBody;
import org.jungppo.bambooforest.response.exception.member.InvalidRefreshTokenException;
import org.jungppo.bambooforest.response.exception.member.RefreshTokenFailureException;
import org.jungppo.bambooforest.security.oauth2.CustomOAuth2User;
import org.jungppo.bambooforest.service.member.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.jungppo.bambooforest.response.ResponseUtil.createSuccessResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/logout")
    public ResponseEntity<ResponseBody<Void>> logout(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        memberService.logout(customOAuth2User.getId(), customOAuth2User.getRegistrationId());
        return ResponseEntity.ok(createSuccessResponse());
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseBody<MemberDto>> getProfile(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        MemberDto memberDto = memberService.getProfile(customOAuth2User.getId());
        return ResponseEntity.ok(createSuccessResponse(memberDto));
    }

    @PostMapping("/reissuance")
    public ResponseEntity<ResponseBody<JwtDto>> reissuanceToken(@RequestHeader(value = "Authorization") String refreshToken) {
        try {
            JwtDto jwtDto = memberService.reissuanceToken(refreshToken);
            return ResponseEntity.ok(createSuccessResponse(jwtDto));
        } catch (InvalidRefreshTokenException e) {   // TODO. 함수형 인터페이스를 이용한 Refactoring
            throw new RefreshTokenFailureException();
        }
    }
}
