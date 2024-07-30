package org.jungppo.bambooforest.global.log.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("within(*..*Controller) && !@annotation(org.jungppo.bambooforest.global.log.aop.NotLogging)")
    public Object requestLogging(final ProceedingJoinPoint joinPoint) throws Throwable {
        final LogInfoDto logInfoDto = new LogInfoDto(joinPoint, joinPoint.getTarget().getClass(), objectMapper);

        try {
            final Object result = joinPoint.proceed(joinPoint.getArgs());
            final String logMessage = objectMapper.writeValueAsString(Map.entry("logInfo", logInfoDto));
            log.info(logMessage);

            return result;
        } catch (final Exception e) {
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            final String exceptionAsString = sw.toString();
            logInfoDto.setException(exceptionAsString);
            final String logMessage = objectMapper.writeValueAsString(logInfoDto);

            log.error(logMessage);
            throw e;
        }
    }
}
