package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestAppConfig.class})
@WebAppConfiguration
public class GraphQLControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    GraphQL graphql;

    private WebTestClient client;

    @Before
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
                .body(Mono.just(request), Map.class)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size(), is(1));

        assertThat(captor.getValue().getQuery(), is(query));
        assertThat(captor.getValue().getVariables(), is(variables));
        assertThat(captor.getValue().getOperationName(), is(operationName));
    }

    @Test
    public void testSimplePostRequest() throws Exception {
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
                .body(Mono.just(request), Map.class)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size(), is(1));

        assertThat(captor.getValue().getQuery(), is(query));
    }


    @Test
    public void testGetRequest() throws Exception {
        String variablesJson = "{\"variable\":\"variableValue\"}";
        String variablesValue = URLEncoder.encode(variablesJson, "UTF-8");
        String query = "query myQuery {foo}";
        String queryString = URLEncoder.encode(query, "UTF-8");
        String operationName = "myQuery";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.get().uri(uriBuilder -> uriBuilder.path("/graphql")
                .queryParam("variables", variablesValue)
                .queryParam("query", queryString)
                .queryParam("operationName", operationName)
                .build(variablesJson, queryString))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size(), is(1));

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("variable", "variableValue");
        assertThat(captor.getValue().getQuery(), is(query));
        assertThat(captor.getValue().getVariables(), is(variables));
        assertThat(captor.getValue().getOperationName(), is(operationName));
    }

    @Test
    public void testSimpleGetRequest() throws Exception {
        String query = "{foo}";
        String queryString = URLEncoder.encode(query, "UTF-8");

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.get().uri(uriBuilder -> uriBuilder.path("/graphql")
                .queryParam("query", queryString)
                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data").isEqualTo("bar");

        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getQuery(), is(query));
    }

}