package ru.netology.cloudservice.helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import ru.netology.cloudservice.models.LoginResponseDto;

import java.lang.reflect.Type;

public class LoginResponseDeserializer implements JsonDeserializer<LoginResponseDto> {

    public LoginResponseDeserializer() {
    }

    @Override
    public LoginResponseDto deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {
        final String authToken = json.getAsJsonObject().get("auth-token").getAsString();
        return new LoginResponseDto(authToken);
    }
}
