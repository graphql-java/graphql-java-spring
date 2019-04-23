package graphql.spring.web.servlet;

import graphql.PublicSpi;

/**
 * An interface for serializing and deserializing GraphQL objects.
 */
@PublicSpi
public interface JsonSerializer {

    /**
     * Serializes the given object to a json {@link String}.
     *
     * @param object the object to serialize
     * @return the json string
     */
    String serialize(Object object);

    /**
     * Deserializes the given json {@link String} to an object of the required type.
     *
     * @param json         the json string
     * @param requiredType the required type
     * @param <T>          the required generic type
     * @return the object
     */
    <T> T deserialize(String json, Class<T> requiredType);
}
