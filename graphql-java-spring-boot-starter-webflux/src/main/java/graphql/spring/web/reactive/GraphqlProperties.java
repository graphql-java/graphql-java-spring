package graphql.spring.web.reactive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * Configuration properties for graphql
 */
@ConfigurationProperties(prefix = "graphql")
public class GraphqlProperties {

    private String httpEndpoint = "/graphql";

    private String websocketEndpoint = "/graphql";

    public String getHttpEndpoint() {
        return httpEndpoint;
    }

    public void setHttpEndpoint(String httpEndpoint) {
        this.httpEndpoint = httpEndpoint;
    }

    public String getWebsocketEndpoint() {
        return websocketEndpoint;
    }

    public void setWebsocketEndpoint(String websocketEndpoint) {
        this.websocketEndpoint = websocketEndpoint;
    }

    @DeprecatedConfigurationProperty(replacement = "graphql.http-endpoint")
    public String getUrl() {
        return this.httpEndpoint;
    }

    public void setUrl(String url) {
        this.httpEndpoint = url;
    }
}