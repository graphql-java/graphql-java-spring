package graphql.spring.web.reactive;

import graphql.ExecutionInput;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * Converts {@link GraphQLInvocationData} to {@link ServerRequest}
 */
public interface ExecutionInputMapper {

    ExecutionInput map(GraphQLInvocationData invocationData, ServerRequest request);
}