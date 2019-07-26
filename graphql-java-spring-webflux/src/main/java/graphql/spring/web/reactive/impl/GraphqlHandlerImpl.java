package graphql.spring.web.reactive.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.reactive.ExecutionInputMapper;
import graphql.spring.web.reactive.GraphQLInvocationData;
import graphql.spring.web.reactive.GraphqlHandler;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GraphqlHandlerImpl implements GraphqlHandler {

    private final GraphQL graphql;

    private final ObjectMapper objectMapper;

    private final ExecutionInputMapper executionInputMapper;

    public GraphqlHandlerImpl(GraphQL graphql, ObjectMapper objectMapper, ExecutionInputMapper executionInputMapper) {
        this.graphql = graphql;
        this.objectMapper = objectMapper;
        this.executionInputMapper = executionInputMapper;
    }

    @Override
    public Mono<ServerResponse> invokeByParams(ServerRequest request) {
        return execute(request, new GraphQLInvocationData(request.queryParam("query").orElse(null)));
    }

    @Override
    public Mono<ServerResponse> invokeByParamsAndBody(ServerRequest request) {
        return request.bodyToMono(String.class).flatMap(it -> execute(request, new GraphQLInvocationData(it)));
    }

    @Override
    public Mono<ServerResponse> invokeByBody(ServerRequest request) {
        return request.bodyToMono(GraphQLInvocationData.class).flatMap(it -> execute(request, it));
    }

    private Mono<ServerResponse> execute(ServerRequest request, GraphQLInvocationData invocationData) {
        try {
            invocationData = objectMapper.updateValue(invocationData, request.queryParams().toSingleValueMap());
            CompletableFuture<ExecutionResult> result = graphql.executeAsync(executionInputMapper.map(invocationData, request));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.fromFuture(result).map(ExecutionResult::toSpecification), Map.class);
        } catch (JsonMappingException e) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Flux.error(e), JsonMappingException.class);
        }
    }
}