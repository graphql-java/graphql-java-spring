package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.reactive.GraphQLContextBuilder;
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

    @Autowired(required = false)
    private GraphQLContextBuilder contextBuilder;

    @Override
    public Mono<ExecutionResult> invoke(GraphQLInvocationData invocationData, ServerWebExchange serverWebExchange) {
        Object context = contextBuilder != null ? contextBuilder.build(serverWebExchange) : null;
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .context(context)
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .build();
        return Mono.fromCompletionStage(graphQL.executeAsync(executionInput));
    }

}
