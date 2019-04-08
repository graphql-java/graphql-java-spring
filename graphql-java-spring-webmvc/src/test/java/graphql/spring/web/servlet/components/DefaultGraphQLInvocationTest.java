package graphql.spring.web.servlet.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.servlet.ExecutionInputCustomizer;
import graphql.spring.web.servlet.GraphQLInvocationData;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        WebRequest webRequest = mock(WebRequest.class);

        ArgumentCaptor<ExecutionInput> captor1 = ArgumentCaptor.forClass(ExecutionInput.class);
        ArgumentCaptor<WebRequest> captor2 = ArgumentCaptor.forClass(WebRequest.class);
        ExecutionInput executionInputResult = mock(ExecutionInput.class);
        when(executionInputCustomizer.customizeExecutionInput(captor1.capture(), captor2.capture())).thenReturn(completedFuture(executionInputResult));

        CompletableFuture<ExecutionResult> invoke = defaultGraphQLInvocation.invoke(graphQLInvocationData, webRequest);

        assertThat(captor1.getValue().getQuery()).isEqualTo(query);
        assertThat(captor1.getValue().getOperationName()).isEqualTo(operationName);
        assertThat(captor1.getValue().getVariables()).isSameAs(variables);

        verify(graphQL).executeAsync(executionInputResult);

    }

}