package org.jungppo.bambooforest.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(order = 99)  // @PreFilter메서드 인터셉터의 순서는 100이므로 99로 설정
public class TransactionConfig {
}
