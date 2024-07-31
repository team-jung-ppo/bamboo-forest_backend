package org.jungppo.bambooforest.global.log.domain;

import org.jungppo.bambooforest.global.log.dto.RequestLogDto;
import org.springframework.stereotype.Component;

@Component
public class LoggingContext {
    private final ThreadLocal<RequestLogDto> currentLoggingForm = ThreadLocal.withInitial(RequestLogDto::new);

    public RequestLogDto getCurrentLoggingForm() {
        return currentLoggingForm.get();
    }

    public void clear() {
        currentLoggingForm.remove();
    }
}
