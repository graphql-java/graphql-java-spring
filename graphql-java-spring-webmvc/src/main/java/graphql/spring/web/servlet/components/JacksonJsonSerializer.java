package graphql.spring.web.servlet.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.spring.web.servlet.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JacksonJsonSerializer implements JsonSerializer {

    private ObjectMapper objectMapper;

    @Autowired
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
