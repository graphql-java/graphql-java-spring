package graphql.spring.web.servlet;

import graphql.ExecutionInput;
import org.springframework.web.servlet.function.ServerRequest;

/**
 * Converts {@link GraphQLInvocationData} to {@link ServerRequest}
 */
public interface ExecutionInputMapper {

    ExecutionInput map(GraphQLInvocationData invocationData, ServerRequest request);
}