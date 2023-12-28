package com.st.dit.cam.auth.config;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AuthCircuitBreakerConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> authUtilityCustomizer(){
        return factory -> {
            factory.configure(builder -> builder
                            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(60)).build())
                            .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.ofDefaults()),
                      "cam-auth-utility");
        };
    }

    @Bean("authCircuitBreaker")
    public CircuitBreaker authCircuitBreaker(CircuitBreakerFactory circuitBreakerFactory){
        return circuitBreakerFactory.create("cam-auth-utility");
    }

}
