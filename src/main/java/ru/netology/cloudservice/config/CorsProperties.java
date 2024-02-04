package ru.netology.cloudservice.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "allowed")
@Getter
@AllArgsConstructor
public class CorsProperties {

    private final String path;
    private final boolean credentials;
    private final String origins;
    private final String methods;
}
