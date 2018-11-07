package graphql.spring.reactive;

import graphql.ExecutionResult;
import graphql.PublicApi;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Mono;

@PublicApi
public interface GraphQLInvocation {

    Mono<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest);

}
