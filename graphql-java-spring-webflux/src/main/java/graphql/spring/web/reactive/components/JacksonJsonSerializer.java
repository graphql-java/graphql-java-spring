package graphql.spring.web.reactive.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.spring.web.reactive.JsonSerializer;

import java.io.IOException;

public class JacksonJsonSerializer implements JsonSerializer {

    private ObjectMapper objectMapper;

    public JacksonJsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing object to JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T deserialize(String json, Class<T> requiredType) {
        try {
            return objectMapper.readValue(json, requiredType);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing object from JSON: " + e.getMessage(), e);
        }
    }
}
