package org.jungppo.bambooforest.global.log.domain;

import org.jungppo.bambooforest.global.log.dto.LogDto;
import org.springframework.stereotype.Component;

@Component
public class LoggingContext {
    private final ThreadLocal<LogDto> currentLoggingForm = ThreadLocal.withInitial(LogDto::new);

    public LogDto getCurrentLoggingForm() {
        return currentLoggingForm.get();
    }

    public void clear() {
        currentLoggingForm.remove();
    }
}
