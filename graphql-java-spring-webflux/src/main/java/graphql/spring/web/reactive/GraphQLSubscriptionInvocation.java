package graphql.spring.web.reactive;

import graphql.PublicApi;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.Map;

@PublicApi
@FunctionalInterface
public interface GraphQLSubscriptionInvocation {

    Flux<Map<String, Object>> invoke(GraphQLInvocationData invocationData, ServerWebExchange serverWebExchange);

}
