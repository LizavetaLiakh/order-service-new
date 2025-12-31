package com.innowise.order.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Testcontainers
@SpringBootTest(properties = "spring.profiles.active=test")
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    static WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("USER_SERVICE_URL", () -> "http://localhost:" + wireMockServer.port());
    }

    @BeforeAll
    static void startContainer() {
        postgres.start();
        wireMockServer.start();
    }

    @AfterAll
    static void stopContainers() {
        wireMockServer.stop();
        postgres.stop();
    }

    @BeforeEach
    void setupWireMock() {
        wireMockServer.stubFor(get(urlPathMatching("/users/get/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"email\":\"abc@gmail.com\",\"name\":\"Polly McDonald\"}")));

        wireMockServer.stubFor(get(urlPathMatching("/users/get/email"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"email\":\"abc@gmail.com\",\"name\":\"Polly McDonald\"}")));
    }
}
