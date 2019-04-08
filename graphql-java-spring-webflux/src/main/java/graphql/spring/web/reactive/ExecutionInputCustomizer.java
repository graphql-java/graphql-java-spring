package graphql.spring.web.reactive;

import graphql.ExecutionInput;
import graphql.PublicApi;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@PublicApi
public interface ExecutionInputCustomizer {

    Mono<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput, ServerWebExchange webRequest);

}
