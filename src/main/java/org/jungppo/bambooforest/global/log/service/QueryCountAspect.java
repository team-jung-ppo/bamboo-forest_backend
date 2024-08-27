package org.jungppo.bambooforest.global.log.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jungppo.bambooforest.global.log.domain.RequestLoggingContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class QueryCountAspect {

    private final RequestLoggingContext requestLoggingContext;

    @Around("execution( * javax.sql.DataSource.getConnection())")
    public Object captureConnection(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Object connection = joinPoint.proceed();

        return new ConnectionProxyInterceptor(connection, requestLoggingContext.getCurrentLoggingForm()).getProxy();
    }
}
