package graphql.spring.web.reactive.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.spring.web.reactive.ExecutionResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Object handleExecutionResult(Mono<ExecutionResult> executionResultMono, ServerHttpResponse serverHttpResponse) {
        return executionResultMono.map(executionResult -> handleImpl(executionResult, serverHttpResponse));
    }

    private Object handleImpl(ExecutionResult executionResult, ServerHttpResponse serverHttpResponse) {
        return executionResult.toSpecification();
    }
}
