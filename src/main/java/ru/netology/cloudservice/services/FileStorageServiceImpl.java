package ru.netology.cloudservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.dto.GetListOfFilesResponseDto;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.exceptions.FileStorageException;
import ru.netology.cloudservice.repositories.UsersFileRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final UsersFileRepository usersFileRepository;

    private AuthServiceImpl authService;

    @Autowired
    public void setAuthService(AuthServiceImpl authService) {
        this.authService = authService;
    }

    public byte[] getFile(String authToken, String fileName) {
        User user = authService.getUserByToken(authToken);
        UserFile file = usersFileRepository.findByUserIdAndFileName(user.getId(), fileName);
        checkFileIsPresentInStorage(file);
        return file.getFileData();
    }

    public void storeFile(User user, MultipartFile file) {
        checkFileNotExistsInStorage(user.getId(), file);
        try {
            UserFile userFile = new UserFile(null, file.getOriginalFilename(), file.getBytes(), user);
            usersFileRepository.save(userFile);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + file.getOriginalFilename());
        }
    }

    public void putFile(User user, String oldFileName, String newFileName) {
        UserFile file = usersFileRepository.findByUserIdAndFileName(user.getId(), oldFileName);
        checkFileIsPresentInStorage(file);
        file.setFileName(newFileName);
        usersFileRepository.save(file);
    }

    public void deleteFile(User user, String fileName) {
        UserFile file = usersFileRepository.findByUserIdAndFileName(user.getId(), fileName);
        checkFileIsPresentInStorage(file);
        usersFileRepository.delete(file);
    }

    public List<GetListOfFilesResponseDto> getListOfFilesResponse(User user, int limit) {
        List<UserFile> userFiles = usersFileRepository.findByUserId(user.getId(), PageRequest.of(0, limit));
        return userFiles.stream()
                        .map(userFile -> new GetListOfFilesResponseDto(userFile.getFileName(), userFile.getFileData().length))
                        .toList();
    }

    private void checkFileNotExistsInStorage(long userId, MultipartFile file) throws FileStorageException {
        if (usersFileRepository.findByUserIdAndFileName(userId, file.getOriginalFilename()) != null) {
            throw new FileStorageException("File with this name already exists id database. Rename file and try again.");
        }
    }

    private void checkFileIsPresentInStorage(UserFile file) throws FileStorageException {
        if (file == null) {
            throw new FileStorageException("File not found.");
        }
    }
}
