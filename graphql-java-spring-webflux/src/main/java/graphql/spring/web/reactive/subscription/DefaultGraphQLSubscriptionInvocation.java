package graphql.spring.web.reactive.subscription;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.GraphQLInvocationData;
import graphql.spring.web.reactive.GraphQLSubscriptionInvocation;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Optional;

@Internal
@Component
public class DefaultGraphQLSubscriptionInvocation implements GraphQLSubscriptionInvocation {

    @Autowired
    private GraphQL graphQL;

    @Autowired
    private Optional<DataLoaderRegistry> optionalDataLoaderRegistry;

    @Autowired
    private ExecutionInputCustomizer executionInputCustomizer;

    @Override
    public Flux<Map<String, Object>> invoke(GraphQLInvocationData invocationData, ServerWebExchange serverWebExchange) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput
                .newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables());
        optionalDataLoaderRegistry.ifPresent(executionInputBuilder::dataLoaderRegistry);
        return executionInputCustomizer
                .customizeExecutionInput(executionInputBuilder.build(), serverWebExchange)
                .map(graphQL::execute)
                .flatMapMany(executionResult -> {
                    if (executionResult.isDataPresent()) {
                        return Flux.from(executionResult.getData());
                    } else {
                        return Flux.empty();
                    }
                })
                .cast(ExecutionResult.class)
                .map(ExecutionResult::toSpecification);
    }
}
