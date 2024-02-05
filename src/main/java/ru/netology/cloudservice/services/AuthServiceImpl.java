package ru.netology.cloudservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.dto.LoginRequestDto;
import ru.netology.cloudservice.entities.User;
import ru.netology.cloudservice.exceptions.BadCredentialsException;
import ru.netology.cloudservice.exceptions.NoSuchUserException;
import ru.netology.cloudservice.jwt.JwtTokenProvider;
import ru.netology.cloudservice.repositories.UsersRepository;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.jwtTokenProvider = new JwtTokenProvider(usersRepository);
    }

    public String login(LoginRequestDto requestDto) {
        Optional<User> optionalUser = usersRepository.findByLoginAndPassword(requestDto.login(), requestDto.password());
        if (!optionalUser.isPresent()) {
            throw new NoSuchUserException("Check request data and try again.");
        }
        User user = optionalUser.get();
        String authToken = jwtTokenProvider.generateToken(user);
        return authToken;
    }

    public void logout(User user) {
        user.setAuthToken(null);
        user.setRoles(Set.of());
        usersRepository.save(user);
    }

    public User getUserByToken(String authToken) {
        authToken = authToken.replace("Bearer ", "");
        Optional<User> optionalUser = usersRepository.findByAuthToken(authToken);
        if (!optionalUser.isPresent()) {
            throw new BadCredentialsException("Invalid token.");
        }
        return optionalUser.get();
    }
}
