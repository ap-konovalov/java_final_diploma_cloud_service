package ru.netology.cloudservice.services;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.entities.UserFile;
import ru.netology.cloudservice.models.GetListOfFilesResponseDto;

import java.util.List;

public interface FileStorageService {

    UserFile getFile(User user, String fileName);

    List<GetListOfFilesResponseDto> getListOfFilesResponse(User user, int limit);

    void storeFile(User user, MultipartFile file);

    void deleteFile(User user, String fileName);
}
