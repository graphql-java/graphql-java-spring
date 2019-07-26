package graphql.spring.web.servlet;

import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Executes graphql query
 */
public interface GraphqlHandler {

    /**
     * Executes graphql query by parameters.
     *
     * @param request {@code ServerRequest}
     * @return {@code Mono<ServerResponse>}
     */
    ServerResponse invokeByParams(ServerRequest request);

    /**
     * Executes graphql query by parameters and body.
     * @param request {@code ServerRequest}
     * @return {@code Mono<ServerResponse>}
     */
    ServerResponse invokeByParamsAndBody(ServerRequest request) throws ServletException, IOException;

    /**
     * Executes graphql query by body.
     * @param request {@code ServerRequest}
     * @return {@code Mono<ServerResponse>}
     */
    ServerResponse invokeByBody(ServerRequest request) throws ServletException, IOException;
}