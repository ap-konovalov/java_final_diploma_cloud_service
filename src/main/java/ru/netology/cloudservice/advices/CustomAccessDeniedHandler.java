package ru.netology.cloudservice.advices;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.netology.cloudservice.dto.ErrorResponseDto;

import java.io.IOException;

import static ru.netology.cloudservice.enums.ErrorCode.BAD_CREDENTIALS_ERROR;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                                                            .id(BAD_CREDENTIALS_ERROR.getCode())
                                                            .message("Access denied: " + accessDeniedException.getMessage())
                                                            .build();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponseDto));
    }
}
