package graphql.spring.web.reactive.config;

/**
 * Helps to configure endpoints for graphql
 */
public interface GraphqlConfigurer {

    String getHttpEndpoint();

    String getWebsocketEndpoint();
}