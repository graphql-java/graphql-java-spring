package graphql.spring.web.servlet.components;

import graphql.spring.web.servlet.*;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
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
    public ServerResponse invokeByParams(ServerRequest request) {
        return execute(request, new GraphQLInvocationData(request.param("query").orElse(null)));
    }

    @Override
    public ServerResponse invokeByParamsAndBody(ServerRequest request) throws ServletException, IOException {
        return execute(request, new GraphQLInvocationData(request.body(String.class)));
    }

    @Override
    public ServerResponse invokeByBody(ServerRequest request) throws ServletException, IOException {
        return execute(request, jsonSerializer.deserialize(request.body(String.class), GraphQLInvocationData.class));
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null) {
            return Collections.emptyMap();
        }
        return jsonSerializer.deserialize(jsonMap, Map.class);
    }

    private ServerResponse execute(ServerRequest request, GraphQLInvocationData invocationData) {
        request.param("operationName").ifPresent(invocationData::setOperationName);
        request.param("variables").map(this::convertVariablesJson).ifPresent(invocationData::setVariables);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(resultHandler.handleExecutionResult(invocation.invoke(invocationData, null)));
    }
}