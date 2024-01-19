package ru.netology.cloudservice.models;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record UserAddRequestDto(@NotBlank(message = "login can not be null") String login,
                                @NotBlank(message = "login can not be null") String password) {

}
