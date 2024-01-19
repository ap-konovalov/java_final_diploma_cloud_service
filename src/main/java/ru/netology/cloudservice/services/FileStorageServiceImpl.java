package ru.netology.cloudservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.exceptions.FileStorageException;
import ru.netology.cloudservice.repositories.UsersFileRepository;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final UsersFileRepository usersFileRepository;

    public File getFile(User user, String filename) {
//        TODO: implement method
        return null;
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
}
