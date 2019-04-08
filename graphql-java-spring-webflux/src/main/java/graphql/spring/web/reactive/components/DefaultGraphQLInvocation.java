package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.GraphQLInvocation;
import graphql.spring.web.reactive.GraphQLInvocationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

    @Autowired
    private GraphQL graphQL;

    @Autowired
    ExecutionInputCustomizer executionInputCustomizer;

    @Override
    public Mono<ExecutionResult> invoke(GraphQLInvocationData invocationData, ServerWebExchange serverWebExchange) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .build();
        Mono<ExecutionInput> customizedExecutionInputMono = executionInputCustomizer.customizeExecutionInput(executionInput, serverWebExchange);
        return customizedExecutionInputMono.flatMap(customizedExecutionInput -> Mono.fromCompletionStage(graphQL.executeAsync(customizedExecutionInput)));
    }

}
