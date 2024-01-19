package ru.netology.cloudservice.controller.authcontroller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.models.LoginRequestDto;
import ru.netology.cloudservice.models.LoginResponseDto;
import ru.netology.cloudservice.providers.RequestBodiesProvider;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginTests {

    private static final String HOST = "http://localhost:";
    private static final String LOGIN_PATH = "/login";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:12.17")
            .withDatabaseName("netology")
            .withUsername("admin")
            .withPassword("admin");

    @Container
    private final GenericContainer<?> cloudServiceApp = new GenericContainer<>("cloud-service:latest")
            .withExposedPorts(5500);

//    @ClassRule
//    public static DockerComposeContainer environment =
//            new DockerComposeContainer(new File("docker-compose.yaml"))
//                    .withExposedService("db_1", 5432, Wait.defaultWaitStrategy())
//                    .withExposedService("backend_1", 5500)
//                    .waitingFor("db_1", Wait.forHealthcheck());

    @Test
    void loginForExistingUserShouldReturnAuthToken() {
//        environment.start();
        Integer appPort = 5500;

        LoginRequestDto loginRequestBody = RequestBodiesProvider.getLoginRequestBody();

        ResponseEntity<LoginResponseDto> response = testRestTemplate.postForEntity(HOST + cloudServiceApp.getMappedPort(5500)
                                                                                           + LOGIN_PATH,
                                                                                   loginRequestBody,
                                                                                   LoginResponseDto.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(!response.getBody().authToken().isBlank());
    }

//    @Test
//    void errorDueToOperationIdValidation() {
//        ConfirmOperationRequestDto requestBody = RequestBodiesProvider.getConfirmOperationRequestBody();
//        requestBody.setOperationId("");
//        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity(HOST +
//                                                                                           cloudServiceApp.getMappedPort(5500) +
//                                                                                           LOGIN_PATH,
//                                                                                   requestBody, ErrorResponseDto.class);
//        assertTrue(response.getStatusCode().is4xxClientError());
//        assertAll(
//                () -> assertEquals(0, response.getBody().getId()),
//                () -> assertEquals("[operationId must not be blank]", response.getBody().getMessage())
//        );
//    }
//
//    @Test
//    void errorDueToCodeValidation() {
//        ConfirmOperationRequestDto requestBody = RequestBodiesProvider.getConfirmOperationRequestBody();
//        requestBody.setCode("");
//        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity(HOST +
//                                                                                           cloudServiceApp.getMappedPort(5500) +
//                                                                                           LOGIN_PATH,
//                                                                                   requestBody, ErrorResponseDto.class);
//        assertTrue(response.getStatusCode().is4xxClientError());
//        assertAll(
//                () -> assertEquals(0, response.getBody().getId()),
//                () -> assertEquals("[code must not be blank]", response.getBody().getMessage())
//        );
//    }
//
//    @Test
//    void errorDueToTransactionNotFound() {
//        ResponseEntity<ErrorResponseDto> response = testRestTemplate.postForEntity(HOST +
//                                                                                           cloudServiceApp.getMappedPort(5500) +
//                                                                                           LOGIN_PATH,
//                                                                                   RequestBodiesProvider.getConfirmOperationRequestBody(),
//                                                                                   ErrorResponseDto.class);
//        assertTrue(response.getStatusCode().is4xxClientError());
//        assertAll(
//                () -> assertEquals(3, response.getBody().getId()),
//                () -> assertEquals("Transaction not found", response.getBody().getMessage())
//        );
//    }
}
