package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.GraphQLInvocationData;
import org.dataloader.DataLoaderRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGraphQLInvocationTest {

    @Mock
    private ObjectProvider<DataLoaderRegistry> dataLoaderRegistry;

    @Test
    public void testCustomizerIsCalled() {

        String query = "query myQuery {foo}";
        String operationName = "myQuery";
        Map<String, Object> variables = new LinkedHashMap<>();

        Mockito.when(dataLoaderRegistry.getIfAvailable(any())).thenReturn(new DataLoaderRegistry());

        GraphQL graphQL = mock(GraphQL.class);
        ExecutionInputCustomizer executionInputCustomizer = mock(ExecutionInputCustomizer.class);
        DefaultGraphQLInvocation defaultGraphQLInvocation = new DefaultGraphQLInvocation(graphQL, executionInputCustomizer, dataLoaderRegistry);
        ExecutionResult executionResult = mock(ExecutionResult.class);
        when(graphQL.executeAsync(any(ExecutionInput.class))).thenReturn(completedFuture(executionResult));

        GraphQLInvocationData graphQLInvocationData = new GraphQLInvocationData(query);
        graphQLInvocationData.setOperationName(operationName);
        graphQLInvocationData.setVariables(variables);
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