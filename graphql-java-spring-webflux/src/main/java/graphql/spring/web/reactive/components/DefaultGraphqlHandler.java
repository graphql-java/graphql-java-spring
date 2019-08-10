package graphql.spring.web.reactive.components;

import graphql.ExecutionResult;
import graphql.spring.web.reactive.*;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

public class DefaultGraphqlHandler implements GraphqlHandler {

    private final GraphQLInvocation invocation;

    private final ExecutionResultHandler resultHandler;

    private final JsonSerializer jsonSerializer;

    public DefaultGraphqlHandler(GraphQLInvocation invocation, ExecutionResultHandler resultHandler, JsonSerializer jsonSerializer) {
        this.invocation = invocation;
        this.resultHandler = resultHandler;
        this.jsonSerializer = jsonSerializer;
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
        return request.bodyToMono(String.class)
                .map(it -> jsonSerializer.deserialize(it, GraphQLInvocationData.class))
                .flatMap(it -> execute(request, it));
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null) {
            return Collections.emptyMap();
        }
        return jsonSerializer.deserialize(jsonMap, Map.class);
    }

    private Mono<ServerResponse> execute(ServerRequest request, GraphQLInvocationData invocationData) {
        request.queryParam("operationName").ifPresent(invocationData::setOperationName);
        request.queryParam("variables").map(this::convertVariablesJson).ifPresent(invocationData::setVariables);
        ServerWebExchange exchange = request.exchange();
        Mono<ExecutionResult> executionResult = invocation.invoke(invocationData, exchange);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body((Mono<Map>) resultHandler.handleExecutionResult(executionResult, exchange.getResponse()), Map.class);
    }
}