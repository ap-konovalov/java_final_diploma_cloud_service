package ru.netology.cloudservice.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.helpers.UserHelper;
import ru.netology.cloudservice.providers.UsersProvider;
import ru.netology.cloudservice.repositories.UsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsersRepositoryTests {

    private static User expectedUser;

    @Autowired
    private UsersRepository usersRepository;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
    }

    @Container
    private static final PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:12.17")
            .withDatabaseName("netology")
            .withUsername("admin")
            .withPassword("admin")
            .withExposedPorts(5432);

    @BeforeEach
    public void setUp() {
        expectedUser = UsersProvider.getUserWithToken();
        usersRepository.deleteAll();
        usersRepository.save(expectedUser);
    }

    @BeforeAll
    public static void init() {
        postgreSqlContainer.start();
    }

    @Test
    void canFindUserByLogin() {
        User actualUser = usersRepository.findByLogin(expectedUser.getLogin()).get();
        UserHelper.checkUserData(expectedUser, actualUser);
    }

    @Test
    void canFindUserByLoginAndPassword() {
        User actualUser = usersRepository.findByLoginAndPassword(expectedUser.getLogin(), expectedUser.getPassword()).get();
        UserHelper.checkUserData(expectedUser, actualUser);
    }

    @Test
    void canFindUserByAuthToken() {
        User actualUser = usersRepository.findByAuthToken(expectedUser.getAuthToken()).get();
        UserHelper.checkUserData(expectedUser, actualUser);
    }

    @Test
    void canNotAddUserWithExistingLoginTwice() {
        assertEquals(1, usersRepository.findAll().size());
        usersRepository.save(expectedUser);
        assertEquals(1, usersRepository.findAll().size());
    }

    @Test
    void canNotAddUserWithExistingId() {
        List<User> users = usersRepository.findAll();
        assertEquals(1, users.size());
        expectedUser.setId(users.get(0).getId());

        usersRepository.save(expectedUser);

        assertEquals(1, usersRepository.findAll().size());
    }
}
