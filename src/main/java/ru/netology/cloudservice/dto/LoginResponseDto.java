package ru.netology.cloudservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDto(@JsonProperty("auth-token") String authToken){

}
