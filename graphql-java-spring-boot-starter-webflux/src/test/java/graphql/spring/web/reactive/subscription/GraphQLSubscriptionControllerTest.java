package graphql.spring.web.reactive.subscription;

import graphql.GraphQL;
import graphql.spring.web.reactive.IntegrationTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true",
        classes = IntegrationTestConfig.class)
@ContextConfiguration(classes = GraphQLSubscriptionAppConfig.class)
public class GraphQLSubscriptionControllerTest {

    @Autowired
    GraphQL graphql;

    @LocalServerPort
    int port;

    @Test
    public void testSubscriptionRequest() {
        String query = "" +
                "    subscription HelloSubscription {\n" +
                "        hello\n" +
                "    }\n";
        WebSocketClient client = new ReactorNettyWebSocketClient();
        URI localWebSocketAddr = URI.create("ws://localhost:" + port + "/subscription");
        AtomicInteger counter = new AtomicInteger(0);
        client.execute(localWebSocketAddr, session -> {
            Flux<Void> receive = session
                    .receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(text -> counter.incrementAndGet())
                    .bufferTimeout(5, Duration.ofSeconds(3))
                    .flatMap(list -> session.close());
            return session
                    .send(Mono.just(session.textMessage(query)))
                    .thenMany(receive)
                    .then();
        }).block(Duration.ofSeconds(10));
        assertThat(counter.intValue(), greaterThanOrEqualTo(5));
    }
}
