package ru.netology.cloudservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDto(@JsonProperty("auth-token") String authToken){

}
