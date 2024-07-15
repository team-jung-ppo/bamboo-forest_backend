package org.jungppo.bambooforest.security.jwt;

import static org.jungppo.bambooforest.response.exception.common.ExceptionType.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.jungppo.bambooforest.response.ExceptionResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(ACCESS_DENIED_EXCEPTION.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter()
			.write(convertToJson(
				new ExceptionResponse(ACCESS_DENIED_EXCEPTION.getCode(), ACCESS_DENIED_EXCEPTION.getMessage())));
	}

	private String convertToJson(ExceptionResponse exceptionResponse) throws IOException {
		return objectMapper.writeValueAsString(exceptionResponse);
	}
}
