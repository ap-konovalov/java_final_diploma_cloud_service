package ru.netology.cloudservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConfigurationPropertiesScan("ru.netology.cloudservice.config")
public class WebConfig implements WebMvcConfigurer{

    private final CorsProperties corsProperties;

    @Autowired
    public WebConfig(CorsProperties corsProperties){
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(corsProperties.getPath())
                .allowCredentials(corsProperties.isCredentials())
                .allowedOrigins(corsProperties.getOrigins())
                .allowedMethods(corsProperties.getMethods());
    }
}
