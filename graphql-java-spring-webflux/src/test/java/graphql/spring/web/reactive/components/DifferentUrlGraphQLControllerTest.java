package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import org.assertj.core.api.Assertions;
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
import testconfig.DifferentUrlTestAppConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DifferentUrlTestAppConfig.class})
@WebAppConfiguration
public class DifferentUrlGraphQLControllerTest {

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
    public void testDifferentUrl() throws Exception {
        Map<String, Object> request = new LinkedHashMap<>();
        String query = "{foo}";
        request.put("query", query);

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        client.post().uri("/otherUrl")
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

}
