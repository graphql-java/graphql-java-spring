package graphql.spring.web.servlet;

import graphql.ExecutionInput;
import graphql.PublicApi;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.CompletableFuture;

@PublicApi
public interface ExecutionInputCustomizer {

    CompletableFuture<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput, WebRequest webRequest);

}
