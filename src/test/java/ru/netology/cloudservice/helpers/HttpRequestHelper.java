package ru.netology.cloudservice.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;
import ru.netology.cloudservice.dto.ErrorResponseDto;
import ru.netology.cloudservice.dto.GetListOfFilesResponseDto;
import ru.netology.cloudservice.dto.LoginResponseDto;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    public void executePost(String endpoint, HttpHeaders headers, MultiValueMap params, MockMultipartFile file) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(endpoint)
                                              .file(file)
                                              .headers(headers)
                                              .params(params)
                                              .contentType(MediaType.MULTIPART_FORM_DATA))
               .andDo(print())
               .andExpect(status().isOk());
    }

    public ErrorResponseDto executePostWithError(String endpoint, HttpHeaders headers, MultiValueMap params, MockMultipartFile file,
                                                 ResultMatcher resultMatcher) throws Exception {
        String responseJson = mockMvc.perform(MockMvcRequestBuilders.multipart(endpoint)
                                                                    .file(file)
                                                                    .headers(headers)
                                                                    .params(params)
                                                                    .contentType(MediaType.MULTIPART_FORM_DATA))
                                     .andDo(print())
                                     .andExpect(resultMatcher)
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString();
        Type listType = new TypeToken<ErrorResponseDto>() {
        }.getType();
        return gson.fromJson(responseJson, listType);
    }

    public byte[] executeGet(String endpoint, HttpHeaders headers, MultiValueMap queryParams) throws Exception {
        return mockMvc.perform(get(endpoint)
                                       .contentType(APPLICATION_JSON)
                                       .headers(headers)
                                       .queryParams(queryParams))
                      .andDo(print())
                      .andExpect(status().isOk())
                      .andReturn()
                      .getResponse()
                      .getContentAsByteArray();
    }

    public <T> T executeGetWithError(String endpoint, HttpHeaders headers, MultiValueMap queryParams, ResultMatcher resultMatcher)
            throws Exception {
        String responseJson = mockMvc.perform(get(endpoint)
                                                      .contentType(APPLICATION_JSON)
                                                      .headers(headers)
                                                      .queryParams(queryParams))
                                     .andDo(print())
                                     .andExpect(resultMatcher)
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString();
        Type listType = new TypeToken<ErrorResponseDto>() {
        }.getType();
        return gson.fromJson(responseJson, listType);
    }

    public <T> T executeGetListOfFiles(String endpoint, HttpHeaders headers, MultiValueMap queryParams) throws Exception {
        String responseJson = mockMvc.perform(get(endpoint)
                                                      .headers(headers)
                                                      .queryParams(queryParams))
                                     .andDo(print())
                                     .andExpect(status().isOk())
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString();
        Type listType = new TypeToken<ArrayList<GetListOfFilesResponseDto>>() {
        }.getType();
        return gson.fromJson(responseJson, listType);
    }

    public <T> T executeGetListOfFilesWithError(String endpoint, HttpHeaders headers, MultiValueMap queryParams,
                                                ResultMatcher resultMatcher) throws Exception {
        String responseJson = mockMvc.perform(get(endpoint)
                                                      .headers(headers)
                                                      .queryParams(queryParams))
                                     .andDo(print())
                                     .andExpect(resultMatcher)
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString();
        Type listType = new TypeToken<ErrorResponseDto>() {
        }.getType();
        return gson.fromJson(responseJson, listType);
    }

    public <T> T executePutWithError(String endpoint, HttpHeaders headers, MultiValueMap queryParams, Object body,
                                     ResultMatcher resultMatcher) throws Exception {
        String responseJson = mockMvc.perform(put(endpoint)
                                                      .contentType(APPLICATION_JSON)
                                                      .headers(headers)
                                                      .queryParams(queryParams)
                                                      .content(gson.toJson(body)))
                                     .andDo(print())
                                     .andExpect(resultMatcher)
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString();

        Type listType = new TypeToken<ErrorResponseDto>() {
        }.getType();
        return gson.fromJson(responseJson, listType);
    }

    public void executePut(String endpoint, HttpHeaders headers, MultiValueMap queryParams, Object body) throws Exception {
        mockMvc.perform(put(endpoint)
                                .contentType(APPLICATION_JSON)
                                .headers(headers)
                                .queryParams(queryParams)
                                .content(gson.toJson(body)))
               .andDo(print())
               .andExpect(status().isOk());
    }

    public void executeDelete(String endpoint, HttpHeaders headers, MultiValueMap queryParams) throws Exception {
        mockMvc.perform(delete(endpoint)
                                .headers(headers)
                                .queryParams(queryParams))
               .andDo(print())
               .andExpect(status().isOk());
    }

    public <T> T executeDeleteWithError(String endpoint, HttpHeaders headers, MultiValueMap queryParams, ResultMatcher resultMatcher)
            throws Exception {
        String responseJson = mockMvc.perform(delete(endpoint)
                                                      .headers(headers)
                                                      .queryParams(queryParams))
                                     .andDo(print())
                                     .andExpect(resultMatcher)
                                     .andReturn()
                                     .getResponse()
                                     .getContentAsString();

        Type listType = new TypeToken<ErrorResponseDto>() {
        }.getType();
        return gson.fromJson(responseJson, listType);
    }
}
