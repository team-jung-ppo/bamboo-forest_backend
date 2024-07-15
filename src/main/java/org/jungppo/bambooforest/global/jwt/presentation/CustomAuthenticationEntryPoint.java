package org.jungppo.bambooforest.global.jwt.presentation;

import static org.jungppo.bambooforest.global.exception.domain.ExceptionType.AUTHENTICATION_ENTRY_POINT_EXCEPTION;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.jungppo.bambooforest.global.exception.dto.ExceptionResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        response.setStatus(AUTHENTICATION_ENTRY_POINT_EXCEPTION.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(convertToJson(new ExceptionResponse(AUTHENTICATION_ENTRY_POINT_EXCEPTION.getCode(),
                        AUTHENTICATION_ENTRY_POINT_EXCEPTION.getMessage())));
    }

    private String convertToJson(final ExceptionResponse exceptionResponse) throws IOException {
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}
