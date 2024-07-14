package org.jungppo.bambooforest.security.jwt;

import static org.jungppo.bambooforest.response.ResponseUtil.*;
import static org.jungppo.bambooforest.response.exception.common.ExceptionType.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.jungppo.bambooforest.response.ResponseBody;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		response.setStatus(AUTHENTICATION_ENTRY_POINT_EXCEPTION.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(convertToJson(createFailureResponse(AUTHENTICATION_ENTRY_POINT_EXCEPTION)));
	}

	private String convertToJson(ResponseBody<Void> response) throws IOException {
		return objectMapper.writeValueAsString(response);
	}
}
