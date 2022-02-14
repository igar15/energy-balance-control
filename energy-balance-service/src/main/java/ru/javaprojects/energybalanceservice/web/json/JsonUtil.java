package ru.javaprojects.energybalanceservice.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import static ru.javaprojects.energybalanceservice.web.json.JacksonObjectMapper.getMapper;


public class JsonUtil {
    private JsonUtil() {
    }

    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return getMapper().readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read from JSON:\n'" + json + "'", e);
        }
    }

    public static <T> String writeValue(T obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid write to JSON:\n'" + obj + "'", e);
        }
    }
}