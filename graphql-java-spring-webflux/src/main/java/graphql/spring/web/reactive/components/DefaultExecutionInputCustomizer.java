package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.Internal;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Internal
public class DefaultExecutionInputCustomizer implements ExecutionInputCustomizer {

    @Override
    public Mono<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput, ServerWebExchange webRequest) {
        return Mono.just(executionInput);
    }
}