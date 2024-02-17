package ru.netology.cloudservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.netology.cloudservice.advices.CustomAccessDeniedHandler;
import ru.netology.cloudservice.advices.CustomAuthenticationEntryPoint;
import ru.netology.cloudservice.jwt.JwtTokenFilter;
import ru.netology.cloudservice.jwt.JwtTokenProvider;

@Configuration
@ConfigurationPropertiesScan("ru.netology.cloudservice.config")
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    public WebConfig(CorsProperties corsProperties, JwtTokenProvider jwtTokenProvider,
                     CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.corsProperties = corsProperties;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(corsProperties.getPath())
                .allowCredentials(corsProperties.isCredentials())
                .allowedOrigins(corsProperties.getOrigins())
                .allowedMethods(corsProperties.getMethods());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .authorizeRequests(authz -> authz
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/**").hasRole("USER")
                    .anyRequest().permitAll())
            .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(customAccessDeniedHandler)
                                                                     .authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }
}
