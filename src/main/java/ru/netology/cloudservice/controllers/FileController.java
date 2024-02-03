package ru.netology.cloudservice.controllers;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.models.GetListOfFilesResponseDto;
import ru.netology.cloudservice.models.PutFileRequestDto;
import ru.netology.cloudservice.services.AuthService;
import ru.netology.cloudservice.services.FileStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class FileController {

    private final AuthService authService;
    private final FileStorageService fileStorageService;

    @SneakyThrows
    @PostMapping(path = "/file", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestHeader("auth-token") String authToken,
                                             @RequestParam(name = "filename") String fileName,
                                             @RequestParam MultipartFile file) {
        User user = authService.getUserByToken(authToken);
        fileStorageService.storeFile(user, file);
        return ResponseEntity.ok("Success upload.");
    }

    @SneakyThrows
    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam(name = "filename") @NotEmpty(message = "must not be empty") String fileName) {
        User user = authService.getUserByToken(authToken);
        fileStorageService.deleteFile(user, fileName);
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @GetMapping("/file")
    public ResponseEntity<byte[]> getFile(@RequestHeader("auth-token") String authToken,
                                          @RequestParam(name = "filename") String fileName) {
        byte[] fileData = fileStorageService.getFile(authToken, fileName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                             .contentType(MediaType.MULTIPART_FORM_DATA)
                             .contentLength(fileData.length)
                             .body(fileData);
    }

    @SneakyThrows
    @PutMapping("/file")
    public ResponseEntity<Void> putFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam(name = "filename") String oldFileName,
                                        @RequestBody() PutFileRequestDto putFileRequestDto) {
        User user = authService.getUserByToken(authToken);
        fileStorageService.putFile(user, oldFileName, putFileRequestDto.filename());
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @GetMapping(path = "/list")
    public ResponseEntity<List<GetListOfFilesResponseDto>> getFiles(@RequestHeader("auth-token") String authToken,
                                                                    @RequestParam
                                                                    @Positive(message = "Limit must be positive number.") int limit) {
        User user = authService.getUserByToken(authToken);
        return ResponseEntity.ok(fileStorageService.getListOfFilesResponse(user, limit));
    }
}
