package ru.netology.cloudservice.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.exceptions.FileStorageException;
import ru.netology.cloudservice.dto.GetListOfFilesResponseDto;
import ru.netology.cloudservice.providers.UsersProvider;
import ru.netology.cloudservice.repositories.UsersFileRepository;
import ru.netology.cloudservice.services.AuthServiceImpl;
import ru.netology.cloudservice.services.FileStorageServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTests {

    private static User user;
    private static final String FIRST_EXPECTED_FILE_NAME = "first.txt";
    private static final String SECOND_EXPECTED_FILE_NAME = "second.png";
    private MultipartFile multipartFile;
    private UserFile firstExpectedUserFile;
    private UserFile secondExpectedUserFile;

    @Mock
    private UsersFileRepository usersFilesRepository;

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    @BeforeAll
    public static void init() {
        user = UsersProvider.getUserWithToken();
        user.setId(1L);
    }

    @BeforeEach
    public void setUp() {
        firstExpectedUserFile = new UserFile(1L, FIRST_EXPECTED_FILE_NAME, "First".getBytes(), user);
        secondExpectedUserFile = new UserFile(2L, SECOND_EXPECTED_FILE_NAME, "Second".getBytes(), user);
        multipartFile = new MockMultipartFile(FIRST_EXPECTED_FILE_NAME, FIRST_EXPECTED_FILE_NAME, "application/octet-stream",
                                              firstExpectedUserFile.getFileData());
    }

    @Test
    public void getFIleShouldReturnFile() {
        when(usersFilesRepository.findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME)).thenReturn(firstExpectedUserFile);
        when(authService.getUserByToken(user.getAuthToken())).thenReturn(user);
        fileStorageService.setAuthService(authService);
        byte[] firstActualUserFile = fileStorageService.getFile(user.getAuthToken(), FIRST_EXPECTED_FILE_NAME);

        assertTrue(Arrays.equals(firstActualUserFile, firstExpectedUserFile.getFileData()));

        verify(usersFilesRepository).findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME);
    }

    @Test
    public void getFIleShouldThrowExceptionIfFileNotExists() {
        when(authService.getUserByToken(user.getAuthToken())).thenReturn(user);
        fileStorageService.setAuthService(authService);
        assertThrows(FileStorageException.class, () -> fileStorageService.getFile(user.getAuthToken(), FIRST_EXPECTED_FILE_NAME));
    }

    @Test
    public void storeFileShouldSaveFile() {
        firstExpectedUserFile.setId(null);

        when(usersFilesRepository.findByUserIdAndFileName(user.getId(), multipartFile.getOriginalFilename())).thenReturn(null);
        fileStorageService.storeFile(user, multipartFile);

        verify(usersFilesRepository).findByUserIdAndFileName(user.getId(), multipartFile.getOriginalFilename());
        verify(usersFilesRepository).save(firstExpectedUserFile);
    }

    @Test
    public void storeFileShouldThrowExceptionIfFileWithSameNameAndUserIdExists() {
        when(usersFilesRepository.findByUserIdAndFileName(user.getId(), multipartFile.getOriginalFilename())).thenReturn(
                firstExpectedUserFile);

        assertThrows(FileStorageException.class, () -> fileStorageService.storeFile(user, multipartFile));
    }

    @Test
    public void putFileShouldSaveFileWithNewFileName() {
        when(usersFilesRepository.findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME)).thenReturn(firstExpectedUserFile);

        fileStorageService.putFile(user, FIRST_EXPECTED_FILE_NAME, SECOND_EXPECTED_FILE_NAME);

        firstExpectedUserFile.setFileName(SECOND_EXPECTED_FILE_NAME);
        verify(usersFilesRepository).findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME);
        verify(usersFilesRepository).save(firstExpectedUserFile);
    }

    @Test
    public void putFileShouldThrowExceptionIfFileNotExists() {
        when(usersFilesRepository.findByUserIdAndFileName(user.getId(), multipartFile.getOriginalFilename())).thenReturn(null);

        assertThrows(FileStorageException.class,
                     () -> fileStorageService.putFile(user, FIRST_EXPECTED_FILE_NAME, SECOND_EXPECTED_FILE_NAME));
    }

    @Test
    public void deleteFileShouldDeleteIt() {
        when(usersFilesRepository.findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME)).thenReturn(firstExpectedUserFile);

        fileStorageService.deleteFile(user, FIRST_EXPECTED_FILE_NAME);

        verify(usersFilesRepository).findByUserIdAndFileName(user.getId(), FIRST_EXPECTED_FILE_NAME);
        verify(usersFilesRepository).delete(firstExpectedUserFile);
    }

    @Test
    public void deleteFileShouldThrowExceptionIfFileNotExists() {
        when(usersFilesRepository.findByUserIdAndFileName(user.getId(), multipartFile.getOriginalFilename())).thenReturn(null);

        assertThrows(FileStorageException.class, () -> fileStorageService.deleteFile(user, FIRST_EXPECTED_FILE_NAME));
    }

    @Test
    public void getListOfFilesShouldReturnListOfFilesWithLimit() {
        int limitFilesInResponse = 2;

        when(usersFilesRepository.findByUserId(user.getId())).thenReturn(
                List.of(firstExpectedUserFile, secondExpectedUserFile, firstExpectedUserFile));
        List<GetListOfFilesResponseDto> response = fileStorageService.getListOfFilesResponse(user, limitFilesInResponse);

        assertEquals(response.size(), limitFilesInResponse);
        verify(usersFilesRepository).findByUserId(user.getId());
    }

    @Test
    public void getListOfFilesShouldReturnFileSizeAndName() {
        int limitFilesInResponse = 2;

        when(usersFilesRepository.findByUserId(user.getId())).thenReturn(List.of(firstExpectedUserFile, secondExpectedUserFile));
        List<GetListOfFilesResponseDto> response = fileStorageService.getListOfFilesResponse(user, limitFilesInResponse);

        assertEquals(getFileResponseByFileName(response, FIRST_EXPECTED_FILE_NAME).size(), firstExpectedUserFile.getFileData().length);
        verify(usersFilesRepository).findByUserId(user.getId());
    }

    @NotNull
    private static GetListOfFilesResponseDto getFileResponseByFileName(List<GetListOfFilesResponseDto> response, String fileName) {
        return response.stream().filter(r -> r.filename().equals(fileName))
                       .findFirst()
                       .orElseThrow(() -> new AssertionError("File should be present in collection."));
    }
}
