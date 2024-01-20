package ru.netology.cloudservice.controllers;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.models.FileResponseDto;
import ru.netology.cloudservice.models.GetListOfFilesResponseDto;
import ru.netology.cloudservice.services.AuthService;
import ru.netology.cloudservice.services.FileStorageService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class FileController {

    private final AuthService authService;
    private final FileStorageService fileStorageService;

    @SneakyThrows
    @GetMapping("/file")
    public ResponseEntity<FileResponseDto> getFile(@RequestHeader("auth-token") String authToken,
                                                   @RequestParam(name = "filename") String fileName) {
        User user = authService.getUserByToken(authToken);
        File file = fileStorageService.getFile(user, fileName);
        byte[] data = Files.readAllBytes(Paths.get(file.getPath()));
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        return ResponseEntity.ok(new FileResponseDto(data, hash));
    }

    @SneakyThrows
    @GetMapping(path = "/list")
    public ResponseEntity<List<GetListOfFilesResponseDto>> getFiles(@RequestHeader("auth-token") String authToken,
                                                                    @RequestParam @Positive(message = "Limit must be positive number.") int limit) {
        User user = authService.getUserByToken(authToken);
        return ResponseEntity.ok(fileStorageService.getListOfFilesResponse(user, limit));
    }

    @SneakyThrows
    @PostMapping(path = "/file", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestHeader("auth-token") String authToken,
                                             @RequestParam(name = "filename") String fileName,
                                             @RequestParam MultipartFile file) {
        User user = authService.getUserByToken(authToken);
        fileStorageService.storeFile(user, file);
        return ResponseEntity.ok("Success upload");
    }
}
