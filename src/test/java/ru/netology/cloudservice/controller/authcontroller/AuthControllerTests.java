package ru.netology.cloudservice.controller.authcontroller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.controller.AbstractControllerTest;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.helpers.HttpRequestHelper;
import ru.netology.cloudservice.dto.LoginRequestDto;
import ru.netology.cloudservice.dto.LoginResponseDto;
import ru.netology.cloudservice.providers.UsersProvider;
import ru.netology.cloudservice.repositories.UsersRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTests extends AbstractControllerTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    MockMvc mockMvc;

    private User user;

    private HttpRequestHelper httpRequestHelper;


    @BeforeEach
    private void setUp() {
        user = UsersProvider.getUserWithToken();
        httpRequestHelper = new HttpRequestHelper(mockMvc);
    }

    @Test
    @SneakyThrows
    void loginForExistingUserShouldSaveAndReturnAuthToken() {
        user.setAuthToken(null);
        usersRepository.save(user);
        LoginRequestDto requestBody = new LoginRequestDto(user.getLogin(), user.getPassword());

        LoginResponseDto response = httpRequestHelper.executePost("/login", requestBody, LoginResponseDto.class);

        assertFalse(response.authToken().isBlank());
        assertFalse(usersRepository.findByLogin(user.getLogin()).get().getAuthToken().isBlank(),
                    "Auth token should be generated and saved in repository.");
    }

    @Test
    @SneakyThrows
    void logoutShouldRemoveToken(){
        usersRepository.save(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Auth-Token", user.getAuthToken());

        httpRequestHelper.executePost("/logout", headers);

        assertNull(usersRepository.findByLogin(user.getLogin()).get().getAuthToken(), "Auth token should be removed when logout.");
    }
}
