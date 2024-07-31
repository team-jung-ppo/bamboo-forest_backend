package org.jungppo.bambooforest.global.log.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jungppo.bambooforest.global.log.domain.LoggingContext;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class QueryCounterAop {

    private final LoggingContext loggingContext;

    public QueryCounterAop(LoggingContext loggingContext) {
        this.loggingContext = loggingContext;
    }

    @Around("execution( * javax.sql.DataSource.getConnection())")
    public Object captureConnection(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Object connection = joinPoint.proceed();

        return new ConnectionProxyInterceptor(connection, loggingContext.getCurrentLoggingForm()).getProxy();
    }
}
