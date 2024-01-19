package ru.netology.cloudservice.services;

import ru.netology.cloudservice.models.UserAddRequestDto;

public interface UsersService {

    void addUser(UserAddRequestDto requestDto);
}
