package graphql.spring.web.reactive.subscription;

import graphql.spring.web.reactive.GraphQLInvocationData;
import graphql.spring.web.reactive.GraphQLSubscriptionInvocation;
import graphql.spring.web.reactive.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class WebFluxWebSocketHandler implements WebSocketHandler {

    @Autowired
    GraphQLSubscriptionInvocation graphQLSubscriptionInvocation;

    @Autowired
    JsonSerializer jsonSerializer;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> receiveMessage = session
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(this::executeRequest)
                .map(jsonSerializer::serialize)
                .map(session::textMessage);
        return session.send(receiveMessage);
    }

    private Flux<Map<String, Object>> executeRequest(String query) {
        GraphQLInvocationData invocationData = new GraphQLInvocationData(query, null, null);
        return graphQLSubscriptionInvocation.invoke(invocationData, null);
    }

}
