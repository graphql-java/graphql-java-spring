package graphql.spring.web.reactive;

import graphql.ExecutionResult;
import graphql.PublicSpi;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@PublicSpi
public interface ExecutionResultHandler {

    Object handleExecutionResult(Mono<ExecutionResult> executionResultMono, ServerHttpResponse serverHttpResponse);
}
