package ru.javaprojects.mealservice.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ru.javaprojects.mealservice.web.json.JacksonObjectMapper.getMapper;

public class JsonUtil {
    private JsonUtil() {
    }

    public static <T> List<T> readValues(String json, Class<T> clazz) {
        ObjectReader reader = getMapper().readerFor(clazz);
        try {
            return reader.<T>readValues(json).readAll();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid read array from JSON:\n'" + json + "'", e);
        }
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

    public static <T> String writeAdditionProps(T obj, String addName, Object addValue) {
        return writeAdditionProps(obj, Map.of(addName, addValue));
    }

    public static <T> String writeAdditionProps(T obj, Map<String, Object> addProps) {
        Map<String, Object> map = getMapper().convertValue(obj, new TypeReference<>() {});
        map.putAll(addProps);
        return writeValue(map);
    }

    public static <T> List<T> readContentFromPage(String json, Class<T> clazz) {
        json = removePageAttributes(json);
        return readValues(json, clazz);
    }

    private static String removePageAttributes(String json) {
        int openSquareBracketIndex = json.indexOf('[');
        int closeSquareBracketIndex = json.indexOf(']');
        return json.substring(openSquareBracketIndex, closeSquareBracketIndex + 1);
    }
}