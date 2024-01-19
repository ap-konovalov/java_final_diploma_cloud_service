package ru.netology.cloudservice.services;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;

import java.io.File;

public interface FileStorageService {

    File getFile(User user, String filename);

    void storeFile(User user, MultipartFile file);
}
