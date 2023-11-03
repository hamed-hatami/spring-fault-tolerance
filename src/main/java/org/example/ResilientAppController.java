package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/api/")
public class ResilientAppController {

    @Autowired
    ExternalAPICaller externalAPICaller;

    @Retry(name = "backendA", fallbackMethod = "fallbackAfterRetry")
    @GetMapping("/retry")
    public String getRetryDetails() {
        return externalAPICaller
                .restTemplate()
                .getForObject("http://localhost:9090/test", String.class);
    }

    @CircuitBreaker(name = "backendB")
    @GetMapping("/circuitBreaker")
    public String getCircuitBreakerDetails() {
        System.out.println("It's called at : " + Instant.now());
        return externalAPICaller
                .restTemplate()
                .getForObject("http://localhost:9090/test", String.class);
    }

    public String fallbackAfterRetry(Exception ex) {
        return "all retries have exhausted";
    }

}
