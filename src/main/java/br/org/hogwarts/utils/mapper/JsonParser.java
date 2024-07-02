package br.org.hogwarts.utils.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.List;

public class JsonParser {

    private JsonParser() {
        throw new IllegalStateException("Utility class");
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @SneakyThrows
    public static String objectToStringJson(Object value) {
        return MAPPER.writeValueAsString(value);
    }

    @SneakyThrows
    public static <T> T stringJsonToObject(String json, Class<T> clazz) {
        return MAPPER.readValue(json, clazz);
    }

    @SneakyThrows
    public static <T> List<T> stringJsonToList(String json, Class<T> clazz) {
        return MAPPER.readValue(json, new TypeReference<List<T>>() {
            @Override
            public Type getType() {
                return MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            }
        });
    }
}
