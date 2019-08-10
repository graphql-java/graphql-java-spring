package graphql.spring.web.servlet.components;

import graphql.ExecutionResult;
import graphql.Internal;
import graphql.spring.web.servlet.ExecutionResultHandler;

import java.util.concurrent.CompletableFuture;

@Internal
public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Override
    public Object handleExecutionResult(CompletableFuture<ExecutionResult> executionResultCF) {
        return executionResultCF.thenApply(ExecutionResult::toSpecification);
    }
}
