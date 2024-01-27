package ru.netology.cloudservice.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudservice.models.LoginResponseDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class HttpRequestHelper {

    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;
    private MockMvc mockMvc;
    private GsonBuilder gb;
    private Gson gson;

    private HttpRequestHelper() {
    }

    public HttpRequestHelper(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        gb = new GsonBuilder();
        gb.registerTypeAdapter(LoginResponseDto.class, new LoginResponseDeserializer());
        gson = gb.create();
    }


    public <T> T executePost(String endpoint, Object body, Class<T> tClass) throws Exception {
        String responeseString = mockMvc.perform(post(endpoint)
                                                         .contentType(APPLICATION_JSON)
                                                         .content(gson.toJson(body)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString();
        return gson.fromJson(responeseString, tClass);
    }

    public void executePost(String endpoint, HttpHeaders headers) throws Exception {
        mockMvc.perform(post(endpoint).headers(headers))
               .andDo(print())
               .andExpect(status().isOk());
    }
}
