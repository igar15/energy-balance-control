package ru.javaprojects.energybalanceservice;

import org.springframework.test.web.servlet.MvcResult;
import ru.javaprojects.energybalanceservice.web.json.JsonUtil;

import java.io.UnsupportedEncodingException;

public class TestUtil {
    private TestUtil() {
    }

    public static String getContent(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString();
    }

    public static <T> T readFromJsonMvcResult(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValue(getContent(result), clazz);
    }
}
