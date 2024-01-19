package ru.netology.cloudservice.services;

import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.models.LoginRequestDto;

public interface AuthService {

    String login(LoginRequestDto requestDto);

    void logout(User authToken);

    User getUserByToken(String authToken);
}
