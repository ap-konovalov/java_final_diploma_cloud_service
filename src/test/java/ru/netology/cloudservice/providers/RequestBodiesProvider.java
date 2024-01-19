package ru.netology.cloudservice.providers;

import lombok.experimental.UtilityClass;
import ru.netology.cloudservice.models.LoginRequestDto;

@UtilityClass
public class RequestBodiesProvider {

    public LoginRequestDto getLoginRequestBody() {
        return new LoginRequestDto("my@mail.ru","qwerty");
    }
}
