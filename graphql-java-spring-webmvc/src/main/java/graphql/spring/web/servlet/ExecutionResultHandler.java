package graphql.spring.web.servlet;

import graphql.ExecutionResult;
import graphql.PublicSpi;
import org.springframework.http.server.ServerHttpResponse;

import java.util.concurrent.CompletableFuture;

@PublicSpi
public interface ExecutionResultHandler {

    Object handleExecutionResult(CompletableFuture<ExecutionResult> executionResultCF, ServerHttpResponse serverHttpResponse);
}
