package com.lawfirm.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.TimeZone;

@SpringBootTest
class BackendApiApplicationTests {

    static {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    void contextLoads() {
    }

}
