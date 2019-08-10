package graphql.spring.web.reactive.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;

/**
 * Deserializer for {@link Map} values
 */
@SuppressWarnings("unchecked")
public class JacksonVariableDeserializer extends JsonDeserializer<Map<String, Object>> {

    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        Object object = p.readValueAs(Object.class);
        if (object instanceof String) {
            ObjectCodec codec = p.getCodec();
            return codec.readValue(codec.getFactory().createParser((String) object), new MapTypeRef());
        }
        return (Map<String, Object>) object;
    }

    private static class MapTypeRef extends TypeReference<Map<String, Object>> {

    }
}