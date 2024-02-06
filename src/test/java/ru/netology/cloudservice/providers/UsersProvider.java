package ru.netology.cloudservice.providers;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import ru.netology.cloudservice.entities.User;

import java.util.Set;

@UtilityClass
public class UsersProvider {

    private static final Faker FAKER = new Faker();

    public User getUser() {
        return new User(null, FAKER.internet().emailAddress(), FAKER.internet().password(), null, null, Set.of());
    }
}
