package ru.netology.cloudservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.exceptions.BadCredentialsException;
import ru.netology.cloudservice.exceptions.NoSuchUserException;
import ru.netology.cloudservice.models.LoginRequestDto;
import ru.netology.cloudservice.providers.UsersProvider;
import ru.netology.cloudservice.repositories.UsersRepository;
import ru.netology.cloudservice.helpers.UserHelper;
import ru.netology.cloudservice.services.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    private static User user;
    private static LoginRequestDto loginRequestDto;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeAll
    public static void init() {
        user = UsersProvider.getUser();
        user.setId(1L);
        loginRequestDto = new LoginRequestDto(user.getLogin(), user.getPassword());
    }

    @Test
    public void loginShouldReturnAuthToken() {
        when(usersRepository.findByLoginAndPassword(user.getLogin(), user.getPassword())).thenReturn(Optional.of(user));
        String authToken = authService.login(loginRequestDto);

        assertFalse(authToken.isBlank());
        verify(usersRepository).findByLoginAndPassword(user.getLogin(), user.getPassword());
    }

    @Test
    public void logoutShouldRemoveAuthToken() {
        authService.logout(user);
        assertThrows(NullPointerException.class, () -> verify(usersRepository).save(user).getAuthToken());
    }

    @Test
    public void getUserByTokenShouldReturnUser() {
        when(usersRepository.findByAuthToken(user.getAuthToken())).thenReturn(Optional.of(user));
        User actualUser = authService.getUserByToken("Bearer " + user.getAuthToken());

        UserHelper.checkUserData(user, actualUser);
        verify(usersRepository).findByAuthToken(user.getAuthToken());
    }

    @Test
    public void getUserByTokenShouldThrowExceptionIfTokenNotExists() {
        assertThrows(BadCredentialsException.class, () -> authService.getUserByToken("Bearer " + user.getAuthToken()));
    }

    @Test
    public void loginShouldThrowExceptionIfUserNotExists() {
        assertThrows(NoSuchUserException.class, () -> authService.login(loginRequestDto));
    }
}
