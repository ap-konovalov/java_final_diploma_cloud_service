package ru.netology.cloudservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.exceptions.UserAddException;
import ru.netology.cloudservice.models.UserAddRequestDto;
import ru.netology.cloudservice.repositories.UsersRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @SneakyThrows
    public void addUser(UserAddRequestDto requestDto) {
        Optional<User> user = usersRepository.findByLogin(requestDto.login());
        if (user.isPresent()) {
            log.info("User with login '" + requestDto.login() + "' already exists in database.");
            throw new UserAddException("Unable to add this user.");
        }
        usersRepository.save(new User(null, requestDto.login(), requestDto.password(), null));
    }
}
