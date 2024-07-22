package org.jungppo.bambooforest.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry(order = 98)  // Transaction의 순서가 99이므로 98로 설정
public class RetryConfig {
}
