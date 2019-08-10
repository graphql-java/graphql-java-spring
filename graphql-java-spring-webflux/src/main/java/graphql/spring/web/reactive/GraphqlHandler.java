package graphql.spring.web.reactive;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
    Mono<ServerResponse> invokeByParams(ServerRequest request);

    /**
     * Executes graphql query by parameters and body.
     * @param request {@code ServerRequest}
     * @return {@code Mono<ServerResponse>}
     */
    Mono<ServerResponse> invokeByParamsAndBody(ServerRequest request);

    /**
     * Executes graphql query by body.
     * @param request {@code ServerRequest}
     * @return {@code Mono<ServerResponse>}
     */
    Mono<ServerResponse> invokeByBody(ServerRequest request);
}