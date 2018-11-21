package graphql.spring.web.servlet;

import graphql.ExecutionResult;
import graphql.PublicSpi;

import java.util.concurrent.CompletableFuture;

@PublicSpi
public interface ExecutionResultHandler {

    Object handleExecutionResult(CompletableFuture<ExecutionResult> executionResultCF);
}
