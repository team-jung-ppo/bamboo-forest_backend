package org.jungppo.bambooforest.global.jwt.presentation;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.ACCESS_DENIED_EXCEPTION;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.global.exception.dto.ExceptionResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
                       final AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(ACCESS_DENIED_EXCEPTION.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(convertToJson(
                        new ExceptionResponse(ACCESS_DENIED_EXCEPTION.getCode(),
                                ACCESS_DENIED_EXCEPTION.getMessage())));
    }

    private String convertToJson(final ExceptionResponse exceptionResponse) throws IOException {
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}
