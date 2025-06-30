package com.nextmove.infra.api_gateway.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nextmove.infra.api_gateway.ApiGatewayApplication;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
        classes = ApiGatewayApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
@AutoConfigureWebTestClient
@Import(com.nextmove.infra.api_gateway.config.TestRoutesConfig.class)
@ImportAutoConfiguration(exclude = {
        org.springframework.cloud.gateway.discovery.GatewayDiscoveryClientAutoConfiguration.class
})
class SecurityIntegrationTest {

    private WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${api.security.token.secret}")
    private String jwtSecret;

    @BeforeAll
    void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();
    }

    @AfterAll
    void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setupStubs() {
        wireMockServer.resetAll();

        wireMockServer.stubFor(get(urlEqualTo("/auth-service/users/me"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("OK")));

        wireMockServer.stubFor(get(urlEqualTo("/auth-service/auth/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("LOGIN_OK")));
    }

    private String generateToken(List<String> roles, long expirationMillis) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        long now = System.currentTimeMillis();

        return JWT.create()
                .withSubject("test-user")
                .withClaim("authorities", roles)
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + expirationMillis))
                .sign(algorithm);
    }

    @Test
    void shouldAllowPublicEndpointWithoutToken() {
        webTestClient.get()
                .uri("/auth-service/auth/login")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("LOGIN_OK");
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() {
        webTestClient.get()
                .uri("/transaction-service/transaction")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.message").exists();
    }

    @Test
    void shouldRejectInvalidJwtToken() {
        webTestClient.get()
                .uri("/transaction-service/transaction")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnForbiddenWhenUserLacksRole() {
        String token = generateToken(List.of("ROLE_USER"), 60_000);

        webTestClient.get()
                .uri("/auth-service/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldAllowAccessWithValidRole() {
        String token = generateToken(List.of("ROLE_SERVICE"), 60_000);

        webTestClient.get()
                .uri("/auth-service/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("OK");
    }
}