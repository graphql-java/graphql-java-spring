package graphql.spring.reactive;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Mono;

@Component
@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

    @Autowired
    private GraphQL graphQL;

    @Override
    public Mono<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .build();
        return Mono.fromCompletionStage(graphQL.executeAsync(executionInput));
    }

}
