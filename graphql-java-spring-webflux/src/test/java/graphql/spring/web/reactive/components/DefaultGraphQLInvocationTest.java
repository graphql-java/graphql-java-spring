package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.GraphQLInvocationData;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultGraphQLInvocationTest {


    @Test
    public void testCustomizerIsCalled() {

        String query = "query myQuery {foo}";
        String operationName = "myQuery";
        Map<String, Object> variables = new LinkedHashMap<>();

        DefaultGraphQLInvocation defaultGraphQLInvocation = new DefaultGraphQLInvocation();
        ExecutionInputCustomizer executionInputCustomizer = mock(ExecutionInputCustomizer.class);
        defaultGraphQLInvocation.executionInputCustomizer = executionInputCustomizer;
        GraphQL graphQL = mock(GraphQL.class);
        defaultGraphQLInvocation.graphQL = graphQL;
        ExecutionResult executionResult = mock(ExecutionResult.class);
        when(graphQL.executeAsync(any(ExecutionInput.class))).thenReturn(completedFuture(executionResult));

        GraphQLInvocationData graphQLInvocationData = new GraphQLInvocationData(query, operationName, variables);
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);

        ArgumentCaptor<ExecutionInput> captor1 = ArgumentCaptor.forClass(ExecutionInput.class);
        ArgumentCaptor<ServerWebExchange> captor2 = ArgumentCaptor.forClass(ServerWebExchange.class);
        ExecutionInput executionInputResult = mock(ExecutionInput.class);
        when(executionInputCustomizer.customizeExecutionInput(captor1.capture(), captor2.capture())).thenReturn(Mono.just(executionInputResult));

        Mono<ExecutionResult> invoke = defaultGraphQLInvocation.invoke(graphQLInvocationData, serverWebExchange);

        assertThat(captor1.getValue().getQuery()).isEqualTo(query);
        assertThat(captor1.getValue().getOperationName()).isEqualTo(operationName);
        assertThat(captor1.getValue().getVariables()).isSameAs(variables);

        invoke.block();

        verify(graphQL).executeAsync(executionInputResult);

    }


}