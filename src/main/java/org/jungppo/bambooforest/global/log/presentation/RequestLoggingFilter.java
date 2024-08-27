package org.jungppo.bambooforest.global.log.presentation;

import static org.jungppo.bambooforest.global.log.domain.MdcType.REQUEST_ID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jungppo.bambooforest.global.log.domain.RequestLoggingContext;
import org.jungppo.bambooforest.global.log.dto.RequestLogDto;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final RequestLoggingContext requestLoggingContext;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        try {
            setupLoggingContext(request);
            filterChain.doFilter(request, response);
        } finally {
            log.info("{}", requestLoggingContext.getCurrentLoggingForm());
            clearLoggingContext();
        }
    }

    private void setupLoggingContext(final HttpServletRequest request) {
        final String requestId = UUID.randomUUID().toString();
        final String requestMethod = request.getMethod();
        final String requestUri = request.getRequestURI();
        final String requestIp = request.getRemoteAddr();

        MDC.put(REQUEST_ID.name(), requestId);
        final RequestLogDto requestLogDto = requestLoggingContext.getCurrentLoggingForm();
        requestLogDto.setRequestDetails(requestMethod, requestUri, requestIp);
    }

    private void clearLoggingContext() {
        MDC.clear();
        requestLoggingContext.clear();
    }
}
