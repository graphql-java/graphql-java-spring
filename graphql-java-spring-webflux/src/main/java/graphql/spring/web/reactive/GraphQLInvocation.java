package graphql.spring.web.reactive;

import graphql.ExecutionResult;
import graphql.PublicApi;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@PublicApi
public interface GraphQLInvocation {

    Mono<ExecutionResult> invoke(GraphQLInvocationData invocationData, ServerWebExchange serverWebExchange);

}
