package ru.netology.cloudservice.models;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record PutFileRequestDto(@NotBlank(message = "filename can not be null") String filename) {

}
