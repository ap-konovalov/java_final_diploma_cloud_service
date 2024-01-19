package ru.netology.cloudservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.entities.User;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginAndPassword(String login, String password);
    Optional<User> findByLogin(String login);
    Optional<User> findByAuthToken(String authToken);
}
