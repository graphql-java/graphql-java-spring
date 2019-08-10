package graphql.spring.web.servlet.config;

import org.springframework.lang.NonNull;

/**
 * Helps to configure endpoints for graphql
 */
public interface GraphqlConfigurer {

    @NonNull
    String getHttpEndpoint();

    @NonNull
    String getWebsocketEndpoint();
}