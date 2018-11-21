package graphql.spring.web.reactive.components;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestAppConfig.class})
@WebAppConfiguration
public class GraphQLControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient client;

    @Before
    public void setup() {
        client = WebTestClient.bindToApplicationContext(applicationContext).build();
    }


    @Test
    public void testPostRequest() throws Exception {
        Map<String, Object> request = new LinkedHashMap<>();
        client.post().uri("/graphql")
                .body(Mono.just(request), Map.class)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data", is("foo"));
//        String query = "{foo}";
//        String variablesJson = "{\"key\":\"value\"}";
//        MvcResult mvcResult = this.mockMvc.perform(get("/graphql")
//                .param("query", query)
//                .param("variables", variablesJson))
//                .andDo(print()).andExpect(status().isOk())
//                .andReturn();

    }

    @Test
    public void testGetRequestWithVariables() throws Exception {
        String variablesJson = "{\"key\":\"value\"}";
        String variablesValue = URLEncoder.encode(variablesJson, "UTF-8");
        String queryString = URLEncoder.encode("{foo}", "UTF-8");
        client.get().uri(uriBuilder -> uriBuilder.path("/graphql")
                .queryParam("variables", variablesValue)
                .queryParam("query", queryString)
                .build(variablesJson, queryString))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("data", is("foo"));

    }
}