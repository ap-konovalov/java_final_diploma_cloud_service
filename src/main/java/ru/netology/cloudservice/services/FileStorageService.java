package ru.netology.cloudservice.services;

import ru.netology.cloudservice.entities.User;

import java.io.File;

public interface FileStorageService {

    File getFile(User user, String filename);
}
