package graphql.spring.web.reactive.components;

import graphql.ExecutionResult;
import graphql.spring.web.reactive.ExecutionResultHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Override
    public Object handleExecutionResult(Mono<ExecutionResult> executionResultMono, ServerHttpResponse serverHttpResponse) {
        return executionResultMono.map(executionResult -> handleImpl(executionResult, serverHttpResponse));
    }

    private Object handleImpl(ExecutionResult executionResult, ServerHttpResponse serverHttpResponse) {
        return executionResult.toSpecification();
    }
}