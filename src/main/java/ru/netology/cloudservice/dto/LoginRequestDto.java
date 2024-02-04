package ru.netology.cloudservice.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record LoginRequestDto(@NotBlank(message = "login can not be null") String login,
                              @NotBlank(message = "login can not be null") String password) {

}
