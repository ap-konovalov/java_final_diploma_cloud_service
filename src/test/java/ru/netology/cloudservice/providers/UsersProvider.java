package ru.netology.cloudservice.providers;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import ru.netology.cloudservice.entities.User;

@UtilityClass
public class UsersProvider {

    private static final Faker FAKER = new Faker();

    public User getUserWithToken() {
        return new User(null, FAKER.internet().emailAddress(), FAKER.internet().password(), FAKER.bothify("????##???"), null);
    }
}
