package graphql.spring.web.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "graphql.url=otherUrl")
public class IntegrationTestDifferentUrl {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void endpointIsAvailableWithDifferentUrl() {
        //Given
        String query = "{foo}";

        String body = this.restTemplate.getForObject("/otherUrl/?query={query}", String.class, query);

        assertThat(body, is("{\"data\":{\"foo\":\"bar\"}}"));
    }
}