package ru.netology.cloudservice.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class AuthTokenGeneratorUtils {

    private final SecureRandom RANDOM = new SecureRandom();

    public String generateAuthToken() {
        byte[] token = new byte[32];
        RANDOM.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }
}
