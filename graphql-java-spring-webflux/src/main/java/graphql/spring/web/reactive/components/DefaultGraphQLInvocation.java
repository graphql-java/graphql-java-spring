package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.GraphQLInvocation;
import graphql.spring.web.reactive.GraphQLInvocationData;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

    private final GraphQL graphQL;

    private final ExecutionInputCustomizer executionInputCustomizer;

    private final ObjectProvider<DataLoaderRegistry> dataLoaderRegistry;

    public DefaultGraphQLInvocation(GraphQL graphQL, ExecutionInputCustomizer executionInputCustomizer, ObjectProvider<DataLoaderRegistry> dataLoaderRegistry) {
        this.graphQL = graphQL;
        this.executionInputCustomizer = executionInputCustomizer;
        this.dataLoaderRegistry = dataLoaderRegistry;
    }

    @Override
    public Mono<ExecutionResult> invoke(GraphQLInvocationData invocationData, ServerWebExchange serverWebExchange) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .dataLoaderRegistry(dataLoaderRegistry.getIfAvailable(DataLoaderRegistry::new))
                .build();
        Mono<ExecutionInput> customizedExecutionInputMono = executionInputCustomizer.customizeExecutionInput(executionInput, serverWebExchange);
        return customizedExecutionInputMono.flatMap(customizedExecutionInput -> Mono.fromCompletionStage(graphQL.executeAsync(customizedExecutionInput)));
    }
}