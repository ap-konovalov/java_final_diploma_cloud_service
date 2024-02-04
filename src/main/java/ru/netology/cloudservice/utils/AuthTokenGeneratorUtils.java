package ru.netology.cloudservice.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class AuthTokenGeneratorUtils {

    private final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;

    public String generateAuthToken() {
        byte[] token = new byte[TOKEN_LENGTH];
        RANDOM.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }
}
