package org.jungppo.bambooforest.global.client.oauth2.kakao.dto;

import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.global.client.oauth2.dto.UnlinkResponse;

@RequiredArgsConstructor
public class KakaoUnlinkSuccessResponse implements UnlinkResponse {

    private final String message;

    @Override
    public String getMessage() {
        return message;
    }
}
