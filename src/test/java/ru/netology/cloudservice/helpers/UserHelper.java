package ru.netology.cloudservice.helpers;

import lombok.experimental.UtilityClass;
import ru.netology.cloudservice.entities.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UtilityClass
public class UserHelper {

    public void checkUserData(User expectedUser, User actualUser) {
        assertAll(
                () -> assertTrue(actualUser.getId() > 0),
                () -> assertTrue(actualUser.getLogin().equals(expectedUser.getLogin())),
                () -> assertTrue(actualUser.getPassword().equals(expectedUser.getPassword())),
                () -> assertTrue(actualUser.getAuthToken().equals(expectedUser.getAuthToken()))
        );
    }
}
