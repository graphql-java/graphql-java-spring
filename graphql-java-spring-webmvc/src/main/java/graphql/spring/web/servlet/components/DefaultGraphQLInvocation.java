package graphql.spring.web.servlet.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.servlet.ExecutionInputCustomizer;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.CompletableFuture;

@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

    private final GraphQL graphql;

    private final ExecutionInputCustomizer customizer;

    private final DataLoaderRegistry registry;

    public DefaultGraphQLInvocation(GraphQL graphql, ExecutionInputCustomizer customizer, ObjectProvider<DataLoaderRegistry> registry) {
        this.graphql = graphql;
        this.customizer = customizer;
        this.registry = registry.getIfAvailable(DataLoaderRegistry::new);
    }

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .dataLoaderRegistry(registry)
                .build();
        CompletableFuture<ExecutionInput> customizedExecutionInput = customizer.customizeExecutionInput(executionInput, webRequest);
        return customizedExecutionInput.thenCompose(graphql::executeAsync);
    }
}