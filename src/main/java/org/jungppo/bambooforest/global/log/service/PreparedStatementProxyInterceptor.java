package org.jungppo.bambooforest.global.log.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jungppo.bambooforest.global.log.dto.LogDto;

@RequiredArgsConstructor
public class PreparedStatementProxyInterceptor implements MethodInterceptor {

    private static final List<String> JDBC_QUERY_METHOD = List.of("executeQuery", "execute", "executeUpdate");

    private final LogDto logDto;

    @Nullable
    @Override
    public Object invoke(@Nonnull final MethodInvocation invocation) throws Throwable {

        final Method method = invocation.getMethod();

        if (JDBC_QUERY_METHOD.contains(method.getName())) {
            final long startTime = System.currentTimeMillis();
            final Object result = invocation.proceed();
            final long endTime = System.currentTimeMillis();

            logDto.addQueryCount();
            logDto.addQueryTime(endTime - startTime);

            return result;
        }

        return invocation.proceed();
    }
}
