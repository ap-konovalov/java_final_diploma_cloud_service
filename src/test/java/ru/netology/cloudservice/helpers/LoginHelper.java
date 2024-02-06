package ru.netology.cloudservice.helpers;

import lombok.experimental.UtilityClass;
import ru.netology.cloudservice.dto.LoginRequestDto;
import ru.netology.cloudservice.dto.LoginResponseDto;
import ru.netology.cloudservice.entities.User;

@UtilityClass
public class LoginHelper {

    public String loginAndGetAuthToken(HttpRequestHelper httpRequestHelper, User user) throws Exception {
        LoginRequestDto requestBody = new LoginRequestDto(user.getLogin(), user.getPassword());
        LoginResponseDto response = httpRequestHelper.executePost("/login", requestBody, LoginResponseDto.class);
        return "Bearer " + response.authToken();
    }
}
