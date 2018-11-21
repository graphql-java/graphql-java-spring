package graphql.spring.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Object handleExecutionResult(CompletableFuture<ExecutionResult> executionResultMono, ServerHttpResponse serverHttpResponse) {
        return executionResultMono.thenApply(executionResult -> handleImpl(executionResult, serverHttpResponse));
    }

    private Object handleImpl(ExecutionResult executionResult, ServerHttpResponse serverHttpResponse) {
        return executionResult.toSpecification();
    }
}
