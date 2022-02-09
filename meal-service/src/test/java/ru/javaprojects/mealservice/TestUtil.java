package ru.javaprojects.mealservice;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.javaprojects.mealservice.web.json.JsonUtil;

import java.io.UnsupportedEncodingException;

public class TestUtil {
    private TestUtil() {
    }

    public static String getContent(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString();
    }

    public static <T> T readFromJson(ResultActions action, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValue(getContent(action.andReturn()), clazz);
    }
}