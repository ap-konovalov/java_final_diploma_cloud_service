package ru.netology.cloudservice.repository;

import org.jetbrains.annotations.NotNull;
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
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.fragments.UserFragment;
import ru.netology.cloudservice.providers.UsersProvider;
import ru.netology.cloudservice.repositories.UsersFileRepository;
import ru.netology.cloudservice.repositories.UsersRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsersFileRepositoryTests {

    private static final String FIRST_EXPECTED_FILE_NAME = "first.txt";
    private static final String SECOND_EXPECTED_FILE_NAME = "second.png";
    private static User expecedUser;
    private static UserFile firstExpectedUserFile;
    private static UserFile secondExpectedUserFile;

    @Autowired
    private UsersFileRepository usersFileRepository;

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
        expecedUser = UsersProvider.getUser();
        usersRepository.save(expecedUser);
        expecedUser = usersRepository.findByLogin(expecedUser.getLogin()).get();
        firstExpectedUserFile = new UserFile(null, FIRST_EXPECTED_FILE_NAME, "First".getBytes(), expecedUser);
        secondExpectedUserFile = new UserFile(null, SECOND_EXPECTED_FILE_NAME, "Second".getBytes(), expecedUser);
        usersFileRepository.save(firstExpectedUserFile);
        usersFileRepository.save(secondExpectedUserFile);
    }

    @BeforeAll
    public static void init() {
        postgreSqlContainer.start();
    }

    @Test
    void canFindFilesByUserId() {
        List<UserFile> actualUserFiles = usersFileRepository.findByUserId(expecedUser.getId());
        checkUserFileResult(firstExpectedUserFile, getFileByName(actualUserFiles, FIRST_EXPECTED_FILE_NAME));
        checkUserFileResult(secondExpectedUserFile, getFileByName(actualUserFiles, SECOND_EXPECTED_FILE_NAME));
    }

    @NotNull
    private static UserFile getFileByName(List<UserFile> actualUserFiles, String secondExpectedFileName) {
        return actualUserFiles.stream().filter(file -> file.getFileName().equals(secondExpectedFileName))
                              .findFirst()
                              .get();
    }

    @Test
    void canFindFileByUserIdAndFileName() {
        UserFile actualUserFile = usersFileRepository.findByUserIdAndFileName(expecedUser.getId(), FIRST_EXPECTED_FILE_NAME);
        checkUserFileResult(firstExpectedUserFile, actualUserFile);
    }

    private static void checkUserFileResult(UserFile expectedUserFile, UserFile actualUserFile) {
        assertAll(
                () -> assertTrue(Arrays.equals(actualUserFile.getFileData(), expectedUserFile.getFileData())),
                () -> assertTrue(actualUserFile.getFileName().equals(expectedUserFile.getFileName())),
                () -> assertTrue(actualUserFile.getId() > 0)
        );
        UserFragment.checkUserData(expecedUser, actualUserFile.getUser());
    }
}
