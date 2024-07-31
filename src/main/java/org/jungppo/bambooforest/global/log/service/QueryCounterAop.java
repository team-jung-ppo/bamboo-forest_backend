package org.jungppo.bambooforest.global.log.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jungppo.bambooforest.global.log.dto.LoggingForm;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class QueryCounterAop {

    private final ThreadLocal<LoggingForm> currentLoggingForm;

    public QueryCounterAop() {
        this.currentLoggingForm = new ThreadLocal<>();
    }

    @Around("execution( * javax.sql.DataSource.getConnection())")
    public Object captureConnection(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Object connection = joinPoint.proceed();

        return new ConnectionProxyHandler(connection, getCurrentLoggingForm()).getProxy();
    }

    private LoggingForm getCurrentLoggingForm() {
        if (currentLoggingForm.get() == null) {
            currentLoggingForm.set(new LoggingForm());
        }

        return currentLoggingForm.get();
    }

    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    public void loggingAfterApiFinish() {
        log.info("{}", getCurrentLoggingForm());
        currentLoggingForm.remove();
    }
}
