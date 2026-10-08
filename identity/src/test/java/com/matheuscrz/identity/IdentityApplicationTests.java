package com.matheuscrz.identity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IdentityApplicationTests extends AbstractIntegrationTest {

    @Test
    @DisplayName("Garante que o ApplicationContext, migrations do Flyway, Redis e Kafka iniciam perfeitamente")
    void contextLoads() {
    }
}