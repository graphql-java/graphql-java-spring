package graphql.spring.web.servlet;

import graphql.ExecutionInput;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "graphql.url=otherUrl")
@ExtendWith(SpringExtension.class)
public class IntegrationTestDifferentUrl {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    GraphQL graphql;

    @Test
    public void endpointIsAvailableWithDifferentUrl() {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        String body = this.restTemplate.getForObject("/otherUrl/?query={query}", String.class, query);

        assertThat(body).isEqualTo("{\"data\":\"bar\"}");
        assertThat(captor.getValue().getQuery()).isEqualTo(query);
    }

}