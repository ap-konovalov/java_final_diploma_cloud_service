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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final UsersFileRepository usersFileRepository;

    public File getFile(User user, String filename) {
//        TODO: implement method
        return null;
    }

    @SneakyThrows
    public List<GetListOfFilesResponseDto> getListOfFilesResponse(User user, int limit) {
        List<UserFile> userFiles = usersFileRepository.findByUserId(user.getId());
        limit = Math.min(userFiles.size(), limit);
        List<GetListOfFilesResponseDto> responseList = new ArrayList<>(limit);
        for (int i = 0; i < limit; i++) {
            responseList.add(new GetListOfFilesResponseDto(userFiles.get(i).getFileName(), userFiles.get(i).getFileData().length));
        }
        return responseList;
    }

    @SneakyThrows
    public void storeFile(User user, MultipartFile file) {
        try {
            UserFile userFile = new UserFile(null, file.getOriginalFilename(), file.getBytes(), user);
            usersFileRepository.save(userFile);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + file.getOriginalFilename());
        }
    }

    public void deleteFile(User user, String fileName) {
        List<UserFile> files = usersFileRepository.findByUserId(user.getId());
        files.stream().forEach(file -> {
            if (file.getFileName().equals(fileName)) {
                usersFileRepository.delete(file);
            }
        });
    }
}
