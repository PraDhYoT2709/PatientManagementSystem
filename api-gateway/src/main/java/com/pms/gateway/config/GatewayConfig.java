package com.pms.gateway.config;

import com.pms.gateway.security.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtFilter) {
        return builder.routes()
                // Auth Service Routes (No JWT required)
                .route("auth-service", r -> r.path("/api/auth/**", "/oauth2/**", "/login/oauth2/**")
                        .uri("lb://auth-service"))
                
                // Patient Service Routes (JWT required)
                .route("patient-service", r -> r.path("/api/patients/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://patient-service"))
                
                // Doctor Service Routes (JWT required)
                .route("doctor-service", r -> r.path("/api/doctors/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://doctor-service"))
                
                // Appointment Service Routes (JWT required)
                .route("appointment-service", r -> r.path("/api/appointments/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://appointment-service"))
                
                // Chatbot Service Routes (JWT required)
                .route("chatbot-service", r -> r.path("/api/chat/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://chatbot-service"))
                
                // Actuator Routes (No JWT required)
                .route("actuator", r -> r.path("/actuator/**")
                        .uri("lb://api-gateway"))
                
                .build();
    }
}