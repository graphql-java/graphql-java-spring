package graphql.spring.web.reactive.impl;

import graphql.ExecutionInput;
import graphql.spring.web.reactive.ExecutionInputMapper;
import graphql.spring.web.reactive.GraphQLInvocationData;
import org.dataloader.DataLoaderRegistry;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;

public class ExecutionInputMapperImpl implements ExecutionInputMapper {

    private final DataLoaderRegistry dataLoaderRegistry;

    public ExecutionInputMapperImpl(DataLoaderRegistry dataLoaderRegistry) {
        this.dataLoaderRegistry = dataLoaderRegistry;
    }

    @Override
    public ExecutionInput map(GraphQLInvocationData invocationData, ServerRequest request) {
        return ExecutionInput.newExecutionInput(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables() != null ? invocationData.getVariables() : new HashMap<>())
                .dataLoaderRegistry(dataLoaderRegistry)
                .build();
    }
}