package ru.netology.cloudservice.advices;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.netology.cloudservice.dto.ErrorResponseDto;

import java.io.IOException;

import static ru.netology.cloudservice.enums.ErrorCode.BAD_CREDENTIALS_ERROR;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                                                            .id(BAD_CREDENTIALS_ERROR.getCode())
                                                            .message("Access denied: " + authException.getMessage())
                                                            .build();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponseDto));
    }
}
