package com.pms.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
class DiscoveryServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
    }
}