package graphql.spring.web.servlet.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.spring.web.servlet.ExecutionResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Object handleExecutionResult(CompletableFuture<ExecutionResult> executionResultMono) {
        return executionResultMono.thenApply(executionResult -> handleImpl(executionResult));
    }

    private Object handleImpl(ExecutionResult executionResult) {
        return executionResult.toSpecification();
    }
}
