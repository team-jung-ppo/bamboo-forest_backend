package org.jungppo.bambooforest.global.status.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class StatusController {
    /**
     * AWS LoadBalancer가 사용할 상태 체크 api
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok().body("Service is up and running");
    }
}
