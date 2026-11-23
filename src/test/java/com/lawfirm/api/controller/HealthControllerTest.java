package com.lawfirm.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HealthControllerTest {

    @Test
    @DisplayName("healthCheck: Debe retornar 200 OK con status UP y service name")
    void healthCheckShouldReturnUp() {
        HealthController controller = new HealthController();
        ResponseEntity<Map<String, Object>> response = controller.healthCheck();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("lawfirm-backend-api", response.getBody().get("service"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}
