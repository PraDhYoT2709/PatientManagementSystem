package com.pms.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=b2D3dM7qA5N8sXzK1JvB4YtR6LcT9QpFgUwErZnHxVbMsD7PfKgVhWnXcYaTrMzB",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class JwtAuthenticationFilterTest {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    private String validToken;
    private String invalidToken;

    @BeforeEach
    void setUp() {
        String secret = "b2D3dM7qA5N8sXzK1JvB4YtR6LcT9QpFgUwErZnHxVbMsD7PfKgVhWnXcYaTrMzB";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        validToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(key)
                .compact();

        invalidToken = "invalid.token.here";
    }

    @Test
    void apply_WithValidToken_ShouldPassRequest() {
        // Given
        ServerHttpRequest request = MockServerHttpRequest.get("/api/patients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = jwtFilter.apply(new JwtAuthenticationFilter.Config());

        // When & Then
        StepVerifier.create(filter.filter(exchange, exchange2 -> Mono.empty()))
                .expectComplete()
                .verify();
    }

    @Test
    void apply_WithInvalidToken_ShouldReturnUnauthorized() {
        // Given
        ServerHttpRequest request = MockServerHttpRequest.get("/api/patients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = jwtFilter.apply(new JwtAuthenticationFilter.Config());

        // When & Then
        StepVerifier.create(filter.filter(exchange, exchange2 -> Mono.empty()))
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void apply_WithMissingToken_ShouldReturnUnauthorized() {
        // Given
        ServerHttpRequest request = MockServerHttpRequest.get("/api/patients")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = jwtFilter.apply(new JwtAuthenticationFilter.Config());

        // When & Then
        StepVerifier.create(filter.filter(exchange, exchange2 -> Mono.empty()))
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void apply_WithAuthEndpoint_ShouldSkipAuthentication() {
        // Given
        ServerHttpRequest request = MockServerHttpRequest.get("/api/auth/login")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = jwtFilter.apply(new JwtAuthenticationFilter.Config());

        // When & Then
        StepVerifier.create(filter.filter(exchange, exchange2 -> Mono.empty()))
                .expectComplete()
                .verify();

        assertNull(exchange.getResponse().getStatusCode());
    }

    @Test
    void apply_WithActuatorEndpoint_ShouldSkipAuthentication() {
        // Given
        ServerHttpRequest request = MockServerHttpRequest.get("/actuator/health")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = jwtFilter.apply(new JwtAuthenticationFilter.Config());

        // When & Then
        StepVerifier.create(filter.filter(exchange, exchange2 -> Mono.empty()))
                .expectComplete()
                .verify();

        assertNull(exchange.getResponse().getStatusCode());
    }
}