package graphql.spring.web.servlet.components;

import graphql.ExecutionInput;
import graphql.Internal;
import graphql.spring.web.servlet.ExecutionInputCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.CompletableFuture;

@Component
@Internal
public class DefaultExecutionInputCustomizer implements ExecutionInputCustomizer {

    @Override
    public CompletableFuture<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput, WebRequest webRequest) {
        return CompletableFuture.completedFuture(executionInput);
    }
}
