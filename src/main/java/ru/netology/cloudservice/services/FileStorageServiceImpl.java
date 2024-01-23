package ru.netology.cloudservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.exceptions.FileStorageException;
import ru.netology.cloudservice.models.GetListOfFilesResponseDto;
import ru.netology.cloudservice.repositories.UsersFileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final UsersFileRepository usersFileRepository;

    @SneakyThrows
    public UserFile getFile(User user, String fileName) {
        UserFile file = usersFileRepository.findByUserIdAndFileName(user.getId(), fileName);
        checkFileIsPresentInStorage(file);
        return file;
    }

    @SneakyThrows
    public void storeFile(User user, MultipartFile file) {
        checkFileNotExistsInStorage(user.getId(), file);
        try {
            UserFile userFile = new UserFile(null, file.getOriginalFilename(), file.getBytes(), user);
            usersFileRepository.save(userFile);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + file.getOriginalFilename());
        }
    }

    @SneakyThrows
    @Override
    public void putFile(User user, String oldFileName, String newFileName) {
        UserFile file = usersFileRepository.findByUserIdAndFileName(user.getId(), oldFileName);
        checkFileIsPresentInStorage(file);
        file.setFileName(newFileName);
        usersFileRepository.save(file);
    }

    @SneakyThrows
    public void deleteFile(User user, String fileName) {
        UserFile file = usersFileRepository.findByUserIdAndFileName(user.getId(), fileName);
        checkFileIsPresentInStorage(file);
        usersFileRepository.delete(file);
    }

    @SneakyThrows
    public List<GetListOfFilesResponseDto> getListOfFilesResponse(User user, int limit) {
        List<UserFile> userFiles = usersFileRepository.findByUserId(user.getId());
        limit = Math.min(userFiles.size(), limit);
        List<GetListOfFilesResponseDto> responseList = new ArrayList<>(limit);
        for (int i = 0; i < limit; i++) {
            String fileName = userFiles.get(i).getFileName();
            if (fileName == null) {
                continue;
            }
            responseList.add(new GetListOfFilesResponseDto(fileName, userFiles.get(i).getFileData().length));
        }
        return responseList;
    }

    private void checkFileNotExistsInStorage(long userId, MultipartFile file) throws FileStorageException {
        if (usersFileRepository.findByUserIdAndFileName(userId, file.getOriginalFilename()) != null) {
            throw new FileStorageException("File with this name already exists id database. Rename file and try again.");
        }
    }

    private static void checkFileIsPresentInStorage(UserFile file) throws FileStorageException {
        if (file == null) {
            throw new FileStorageException("File not found.");
        }
    }
}
