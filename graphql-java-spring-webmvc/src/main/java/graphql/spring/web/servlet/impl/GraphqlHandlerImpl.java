package graphql.spring.web.servlet.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.servlet.ExecutionInputMapper;
import graphql.spring.web.servlet.GraphQLInvocationData;
import graphql.spring.web.servlet.GraphqlHandler;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
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
    public ServerResponse invokeByParams(ServerRequest request) {
        return execute(request, new GraphQLInvocationData(request.param("query").orElse(null)));
    }

    @Override
    public ServerResponse invokeByParamsAndBody(ServerRequest request) throws ServletException, IOException {
        return execute(request, new GraphQLInvocationData(request.body(String.class)));
    }

    @Override
    public ServerResponse invokeByBody(ServerRequest request) throws ServletException, IOException {
        return execute(request, request.body(GraphQLInvocationData.class));
    }

    private ServerResponse execute(ServerRequest request, GraphQLInvocationData invocationData) {
        try {
            invocationData = objectMapper.updateValue(invocationData, request.params().toSingleValueMap());
            CompletableFuture<ExecutionResult> result = graphql.executeAsync(executionInputMapper.map(invocationData, request));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result.thenApply(ExecutionResult::toSpecification));
        } catch (JsonMappingException e) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e);
        }
    }
}