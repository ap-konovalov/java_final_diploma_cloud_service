package ru.netology.cloudservice.controller.filescontroller;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.controller.AbstractControllerTest;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.helpers.HttpRequestHelper;
import ru.netology.cloudservice.models.GetListOfFilesResponseDto;
import ru.netology.cloudservice.models.PutFileRequestDto;
import ru.netology.cloudservice.providers.UsersProvider;
import ru.netology.cloudservice.repositories.UsersFileRepository;
import ru.netology.cloudservice.repositories.UsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FilesControllerTests extends AbstractControllerTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersFileRepository usersFileRepository;

    @Autowired
    MockMvc mockMvc;

    private User user;
    private HttpRequestHelper httpRequestHelper;
    private HttpHeaders headers;
    private MultiValueMap queryParams;
    private static final String FIRST_EXPECTED_FILE_NAME = "first.txt";
    private static final String SECOND_EXPECTED_FILE_NAME = "second.png";
    private static final String FILENAME_QUERY_PARAM = "filename";
    private static final String FILE_ENDPOINT = "/file";
    private static UserFile firstExpectedUserFile;
    private static UserFile secondExpectedUserFile;


    @BeforeEach
    private void setUp() {
        usersFileRepository.deleteAll();
        httpRequestHelper = new HttpRequestHelper(mockMvc);
        user = UsersProvider.getUserWithToken();
        usersRepository.save(user);
        headers = new HttpHeaders();
        headers.add("auth-token", user.getAuthToken());
        queryParams = new LinkedMultiValueMap();
        firstExpectedUserFile = new UserFile(1L, FIRST_EXPECTED_FILE_NAME, "First".getBytes(), user);
        secondExpectedUserFile = new UserFile(2L, SECOND_EXPECTED_FILE_NAME, "Second".getBytes(), user);
    }

    @Test
    @SneakyThrows
    void getFileShouldReturnStoredFile() {
        usersFileRepository.save(firstExpectedUserFile);
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);

        byte[] responseFile = httpRequestHelper.executeGet(FILE_ENDPOINT, headers, queryParams);

        assertArrayEquals(responseFile, firstExpectedUserFile.getFileData());
    }

    @Test
    @SneakyThrows
    void postFileShouldSaveFileInStorage() {
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);
        MockMultipartFile multipartFile = new MockMultipartFile("file", FIRST_EXPECTED_FILE_NAME, MediaType.TEXT_PLAIN.getType(),
                                                                firstExpectedUserFile.getFileData());

        httpRequestHelper.executePost(FILE_ENDPOINT, headers, queryParams, multipartFile);

        UserFile responseFile = usersFileRepository.findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME);
        assertArrayEquals(responseFile.getFileData(), firstExpectedUserFile.getFileData());
    }

    @Test
    @SneakyThrows
    void putFileShouldChangeFilename() {
        usersFileRepository.save(firstExpectedUserFile);
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);
        PutFileRequestDto requestBody = new PutFileRequestDto(SECOND_EXPECTED_FILE_NAME);

        httpRequestHelper.executePut(FILE_ENDPOINT, headers, queryParams, requestBody);

        UserFile responseFile = usersFileRepository.findByUserIdAndFileName(user.getId(), SECOND_EXPECTED_FILE_NAME);
        assertArrayEquals(responseFile.getFileData(), firstExpectedUserFile.getFileData());
    }

    @Test
    @SneakyThrows
    void deleteFileShouldRemoveItFromStorage() {
        usersFileRepository.save(firstExpectedUserFile);
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);

        httpRequestHelper.executeDelete(FILE_ENDPOINT, headers, queryParams);

        UserFile responseFile = usersFileRepository.findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME);
        assertNull(responseFile);
    }

    @Test
    @SneakyThrows
    void getFilesShouldReturnFilesInfo() {
        usersFileRepository.saveAll(List.of(firstExpectedUserFile, secondExpectedUserFile));
        int responseFilesInfoLimit = 2;
        queryParams.add("limit", String.valueOf(responseFilesInfoLimit));

        List<GetListOfFilesResponseDto> responseFilesInfo = httpRequestHelper.executeGetListOfFiles("/list", headers, queryParams);

        assertEquals(responseFilesInfoLimit, responseFilesInfo.size());
        assertTrue(getFileResponseByFileName(responseFilesInfo, FIRST_EXPECTED_FILE_NAME).size() ==
                           firstExpectedUserFile.getFileData().length);
        assertTrue(getFileResponseByFileName(responseFilesInfo, SECOND_EXPECTED_FILE_NAME).size() ==
                           secondExpectedUserFile.getFileData().length);
    }


    @NotNull
    private static GetListOfFilesResponseDto getFileResponseByFileName(List<GetListOfFilesResponseDto> response, String fileName) {
        return response.stream().filter(r -> r.filename().equals(fileName))
                       .findFirst()
                       .orElseThrow(() -> new AssertionError("File should be present in collection."));
    }
}
