package ru.netology.cloudservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudservice.models.UserAddRequestDto;
import ru.netology.cloudservice.services.UsersService;

@RestController
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/user")
    public ResponseEntity<String> addUser(@RequestBody @Valid UserAddRequestDto requestDto) {
        usersService.addUser(requestDto);
        return ResponseEntity.ok("User added");
    }
}
