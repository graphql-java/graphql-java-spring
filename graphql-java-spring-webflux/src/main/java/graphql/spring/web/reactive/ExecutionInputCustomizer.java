package graphql.spring.web.reactive;

import graphql.ExecutionInput;
import graphql.PublicApi;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Lets you customize the #ExecutionInput before the query is executed.
 * You can for example set a context object or define a root value.
 * <p>
 * This is only used if you use the default {@link GraphQLInvocation}.
 */
@PublicApi
public interface ExecutionInputCustomizer {

    Mono<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput, ServerWebExchange webRequest);

}
