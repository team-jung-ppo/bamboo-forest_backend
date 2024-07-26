package org.jungppo.bambooforest;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BambooForestBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BambooForestBackendApplication.class, args);
    }

    @PostConstruct
    public void initializeTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
