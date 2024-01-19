package ru.netology.cloudservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.exceptions.NoSuchUserException;
import ru.netology.cloudservice.models.LoginRequestDto;
import ru.netology.cloudservice.repositories.UsersRepository;
import ru.netology.cloudservice.utils.AuthTokenGeneratorUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;

    @SneakyThrows
    public String login(LoginRequestDto requestDto) {
        Optional<User> optionalUser = usersRepository.findByLoginAndPassword(requestDto.login(), requestDto.password());
        checkUserIsPresentInDb(optionalUser);
        User user = optionalUser.get();
        String authToken = getOrGenerateToken(user);
        saveTokenInDb(user, authToken);
        return authToken;
    }

    public void logout(User user) {
        user.setAuthToken(null);
        usersRepository.save(user);
    }

    @SneakyThrows
    public User getUserByToken(String authToken) {
        authToken = authToken.replace("Bearer ", "");
        Optional<User> optionalUser = usersRepository.findByAuthToken(authToken);
        checkUserIsPresentInDb(optionalUser);
        return optionalUser.get();
    }

    private void saveTokenInDb(User user, String token) {
        user.setAuthToken(token);
        usersRepository.save(user);
    }

    private static String getOrGenerateToken(User user) {
        return user.getAuthToken() == null ? AuthTokenGeneratorUtils.generateAuthToken() : user.getAuthToken();
    }

    private static void checkUserIsPresentInDb(Optional<User> user) throws NoSuchUserException {
        if (!user.isPresent()) {
            throw new NoSuchUserException("Check request data and try again.");
        }
    }
}
