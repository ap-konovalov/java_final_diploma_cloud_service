package ru.netology.cloudservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.models.LoginRequestDto;
import ru.netology.cloudservice.models.LoginResponseDto;
import ru.netology.cloudservice.services.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
        String authToken = authService.login(requestDto);
        return ResponseEntity.ok(new LoginResponseDto(authToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(name = "Auth-Token") String authToken) {
        User user = authService.getUserByToken(authToken);
        authService.logout(user);
        return ResponseEntity.ok("");
    }
}
