package ru.netology.cloudservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${allowed.path}")
    private String allowedPath;

    @Value("${allowed.credentials}")
    private boolean allowedCredentials;

    @Value("${allowed.origins}")
    private String allowedOrigins;

    @Value("${allowed.methods}")
    private String allowedMethods;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(allowedPath)
                .allowCredentials(allowedCredentials)
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods);
    }
}
