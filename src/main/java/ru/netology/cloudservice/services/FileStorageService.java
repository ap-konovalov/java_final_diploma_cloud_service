package ru.netology.cloudservice.services;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.models.GetListOfFilesResponseDto;

import java.util.List;

public interface FileStorageService {

    byte[] getFile(String authToken, String fileName);

    void storeFile(User user, MultipartFile file);

    void putFile(User user, String oldFileName, String newFileName);

    void deleteFile(User user, String fileName);

    List<GetListOfFilesResponseDto> getListOfFilesResponse(User user, int limit);
}
