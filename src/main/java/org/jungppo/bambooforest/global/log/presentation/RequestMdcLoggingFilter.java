package org.jungppo.bambooforest.global.log.presentation;

import static org.jungppo.bambooforest.global.log.domain.MdcType.REQUEST_ID;
import static org.jungppo.bambooforest.global.log.domain.MdcType.REQUEST_IP;
import static org.jungppo.bambooforest.global.log.domain.MdcType.REQUEST_URI;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestMdcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            setMdc(request);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private void setMdc(HttpServletRequest request) {
        MDC.put(REQUEST_ID.name(), UUID.randomUUID().toString());
        MDC.put(REQUEST_URI.name(), request.getMethod() + " " + request.getRequestURI());
        MDC.put(REQUEST_IP.name(), request.getRemoteAddr());
    }
}
