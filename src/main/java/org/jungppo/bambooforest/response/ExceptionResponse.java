package org.jungppo.bambooforest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
	private final String code;
	private final String message;
}
