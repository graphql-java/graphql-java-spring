package graphql.spring.web.reactive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "graphql.url=otherUrl")
@RunWith(SpringRunner.class)
public class IntegrationDifferentUrlTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void endpointIsAvailableWithDifferentUrl() {
        //Given
        String query = "{foo}";

        webClient.get().uri("/otherUrl?query={query}", query)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("{\"data\":{\"foo\":\"bar\"}}");
    }

}