package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testconfig.TestAppConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestAppConfig.class})
@WebAppConfiguration
public class GraphQLControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    GraphQL graphql;

    private WebTestClient client;

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToApplicationContext(applicationContext).build();
    }


    @Test
    public void testPostRequest() throws Exception {
        Map<String, Object> request = new LinkedHashMap<>();
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("variable", "variableValue");
        String query = "query myQuery {foo}";
        request.put("query", query);
        request.put("variables", variables);
        String operationName = "myQuery";
        request.put("operationName", operationName);

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Map.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size()).isEqualTo(1);

        assertThat(captor.getValue().getQuery()).isEqualTo(query);
        assertThat(captor.getValue().getVariables()).isEqualTo(variables);
        assertThat(captor.getValue().getOperationName()).isEqualTo(operationName);
    }

    @Test
    public void testSimplePostRequest() {
        Map<String, Object> request = new LinkedHashMap<>();
        String query = "{foo}";
        request.put("query", query);

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Map.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size()).isEqualTo(1);

        assertThat(captor.getValue().getQuery()).isEqualTo(query);
    }

    @Test
    public void testQueryParamPostRequest() throws Exception {
        String variablesJson = "{\"variable\":\"variableValue\"}";
        String query = "query myQuery {foo}";
        String operationName = "myQuery";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.post().uri(uriBuilder -> uriBuilder.path("/graphql")
                .queryParam("variables", "{variables}")
                .queryParam("query", "{query}")
                .queryParam("operationName", "{operationName}")
                .build(variablesJson, query, operationName))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size()).isEqualTo(1);

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("variable", "variableValue");
        assertThat(captor.getValue().getQuery()).isEqualTo(query);
        assertThat(captor.getValue().getVariables()).isEqualTo(variables);
        assertThat(captor.getValue().getOperationName()).isEqualTo(operationName);
    }

    @Test
    public void testSimpleQueryParamPostRequest() throws Exception {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.post().uri(uriBuilder -> uriBuilder.path("/graphql")
                .queryParam("query", "{query}")
                .build(query))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");


        assertThat(captor.getAllValues().size()).isEqualTo(1);

        assertThat(captor.getValue().getQuery()).isEqualTo(query);
    }

    @Test
    public void testApplicationGraphqlPostRequest() throws Exception {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.post().uri("/graphql")
                .contentType(new MediaType("application", "graphql"))
                .body(Mono.just(query), String.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size()).isEqualTo(1);

        assertThat(captor.getValue().getQuery()).isEqualTo(query);
    }

    @Test
    public void testGetRequest() throws Exception {
        String variablesJson = "{\"variable\":\"variableValue\"}";
        String query = "query myQuery {foo}";
        String operationName = "myQuery";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.get().uri(uriBuilder -> uriBuilder.path("/graphql")
                .queryParam("variables", "{variables}")
                .queryParam("query", "{query}")
                .queryParam("operationName", "{operationName}")
                .build(variablesJson, query, operationName))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size()).isEqualTo(1);

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("variable", "variableValue");

        assertThat(captor.getValue().getQuery()).isEqualTo(query);
        assertThat(captor.getValue().getVariables()).isEqualTo(variables);
        assertThat(captor.getValue().getOperationName()).isEqualTo(operationName);
    }

    @Test
    public void testSimpleGetRequest() throws Exception {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.get().uri(uriBuilder -> uriBuilder.path("/graphql")
                .queryParam("query", "{query}")
                .build(query))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size()).isEqualTo(1);

        assertThat(captor.getValue().getQuery()).isEqualTo(query);
    }

}
