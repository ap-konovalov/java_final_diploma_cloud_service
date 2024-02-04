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
import ru.netology.cloudservice.dto.ErrorResponseDto;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.helpers.HttpRequestHelper;
import ru.netology.cloudservice.dto.GetListOfFilesResponseDto;
import ru.netology.cloudservice.dto.PutFileRequestDto;
import ru.netology.cloudservice.providers.UsersProvider;
import ru.netology.cloudservice.repositories.UsersFileRepository;
import ru.netology.cloudservice.repositories.UsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cloudservice.enums.ErrorCode.BAD_CREDENTIALS_ERROR;
import static ru.netology.cloudservice.enums.ErrorCode.FILE_STORAGE_ERROR;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FilesControllerTests extends AbstractControllerTest {

    private static final String INVALID_TOKEN_ERROR_MESSAGE = "Invalid token.";
    private static final String FILE_NOT_FOUND_ERROR_MESSAGE = "File not found.";
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
    void getFileShouldReturnErrorIfFileNotPresentInStorage() {
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);

        ErrorResponseDto response = httpRequestHelper.executeGetWithError(FILE_ENDPOINT, headers, queryParams, status().isBadRequest());

        assertEquals(FILE_NOT_FOUND_ERROR_MESSAGE, response.getMessage());
        assertEquals(FILE_STORAGE_ERROR.getCode(), response.getId());
    }

    @Test
    @SneakyThrows
    void getFileShouldReturnErrorIfUserNotFoundByToken() {
        usersRepository.deleteAll();
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);

        ErrorResponseDto response = httpRequestHelper.executeGetWithError(FILE_ENDPOINT, headers, queryParams, status().isUnauthorized());

        assertEquals(INVALID_TOKEN_ERROR_MESSAGE, response.getMessage());
        assertEquals(BAD_CREDENTIALS_ERROR.getCode(), response.getId());
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
    void postFileShouldReturnErrorIfUserNotFoundByToken() {
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);
        usersRepository.deleteAll();
        MockMultipartFile multipartFile = new MockMultipartFile("file", FIRST_EXPECTED_FILE_NAME, MediaType.TEXT_PLAIN.getType(),
                                                                firstExpectedUserFile.getFileData());

        ErrorResponseDto response = httpRequestHelper.executePostWithError(FILE_ENDPOINT, headers, queryParams, multipartFile,
                                                                           status().isUnauthorized());

        assertEquals(INVALID_TOKEN_ERROR_MESSAGE, response.getMessage());
        assertEquals(BAD_CREDENTIALS_ERROR.getCode(), response.getId());
    }

    @Test
    @SneakyThrows
    void postFileShouldReturnErrorIfFileWithSameNameAlreadyPresentInStorage() {
        usersFileRepository.save(firstExpectedUserFile);
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);
        MockMultipartFile multipartFile = new MockMultipartFile("file", FIRST_EXPECTED_FILE_NAME, MediaType.TEXT_PLAIN.getType(),
                                                                firstExpectedUserFile.getFileData());

        ErrorResponseDto response = httpRequestHelper.executePostWithError(FILE_ENDPOINT, headers, queryParams, multipartFile,
                                                                           status().isBadRequest());

        assertEquals("File with this name already exists id database. Rename file and try again.", response.getMessage());
        assertEquals(FILE_STORAGE_ERROR.getCode(), response.getId());
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
    void putFileShouldReturnErrorIfUserNotFoundByToken() {
        usersRepository.deleteAll();
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);
        PutFileRequestDto requestBody = new PutFileRequestDto(SECOND_EXPECTED_FILE_NAME);

        ErrorResponseDto response = httpRequestHelper.executePutWithError(FILE_ENDPOINT, headers, queryParams, requestBody,
                                                                          status().isUnauthorized());

        assertEquals(INVALID_TOKEN_ERROR_MESSAGE, response.getMessage());
        assertEquals(BAD_CREDENTIALS_ERROR.getCode(), response.getId());
    }

    @Test
    @SneakyThrows
    void putFileShouldReturnErrorIfFileNotFound() {
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);
        PutFileRequestDto requestBody = new PutFileRequestDto(SECOND_EXPECTED_FILE_NAME);

        ErrorResponseDto response = httpRequestHelper.executePutWithError(FILE_ENDPOINT, headers, queryParams, requestBody,
                                                                          status().isBadRequest());

        assertEquals(FILE_NOT_FOUND_ERROR_MESSAGE, response.getMessage());
        assertEquals(FILE_STORAGE_ERROR.getCode(), response.getId());
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
    void deleteFileShouldReturnErrorIfUserNotFoundByToken() {
        usersRepository.deleteAll();
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);

        ErrorResponseDto response = httpRequestHelper.executeDeleteWithError(FILE_ENDPOINT, headers, queryParams,
                                                                             status().isUnauthorized());

        assertEquals(INVALID_TOKEN_ERROR_MESSAGE, response.getMessage());
        assertEquals(BAD_CREDENTIALS_ERROR.getCode(), response.getId());
    }

    @Test
    @SneakyThrows
    void deleteFileShouldReturnErrorIfFileNotFound() {
        queryParams.add(FILENAME_QUERY_PARAM, FIRST_EXPECTED_FILE_NAME);

        ErrorResponseDto response = httpRequestHelper.executeDeleteWithError(FILE_ENDPOINT, headers, queryParams, status().isBadRequest());

        assertEquals(FILE_NOT_FOUND_ERROR_MESSAGE, response.getMessage());
        assertEquals(FILE_STORAGE_ERROR.getCode(), response.getId());
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

    @Test
    @SneakyThrows
    void getFilesShouldReturnErrorIfUserNotFoundByToken() {
        usersRepository.deleteAll();
        queryParams.add("limit", String.valueOf(2));

        ErrorResponseDto response = httpRequestHelper.executeGetListOfFilesWithError("/list", headers,
                                                                                     queryParams,
                                                                                     status().isUnauthorized());

        assertEquals(INVALID_TOKEN_ERROR_MESSAGE, response.getMessage());
        assertEquals(BAD_CREDENTIALS_ERROR.getCode(), response.getId());
    }


    @NotNull
    private GetListOfFilesResponseDto getFileResponseByFileName(List<GetListOfFilesResponseDto> response, String fileName) {
        return response.stream().filter(r -> r.filename().equals(fileName))
                       .findFirst()
                       .orElseThrow(() -> new AssertionError("File should be present in collection."));
    }
}
