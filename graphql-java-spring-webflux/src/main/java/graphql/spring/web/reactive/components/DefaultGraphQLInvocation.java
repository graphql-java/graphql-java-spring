package graphql.spring.web.reactive.components;

import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.GraphQLInvocation;
import graphql.spring.web.reactive.GraphQLInvocationData;
import graphql.spring.web.reactive.OnDemandDataLoaderRegistry;
import reactor.core.publisher.Mono;

@Component
@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

	@Autowired
	GraphQL graphQL;

	@Autowired(required = false)
	DataLoaderRegistry dataLoaderRegistry;

	@Autowired(required = false)
	OnDemandDataLoaderRegistry onDemandDataLoaderRegistry;

	@Autowired
	ExecutionInputCustomizer executionInputCustomizer;

	@Override
	public Mono<ExecutionResult> invoke(GraphQLInvocationData invocationData, ServerWebExchange serverWebExchange) {
		ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
				.query(invocationData.getQuery()).operationName(invocationData.getOperationName())
				.variables(invocationData.getVariables());
		if (onDemandDataLoaderRegistry != null) {
			executionInputBuilder.dataLoaderRegistry(onDemandDataLoaderRegistry.getNewDataLoaderRegistry());
		} else if (dataLoaderRegistry != null) {
			executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
		}
		ExecutionInput executionInput = executionInputBuilder.build();
		Mono<ExecutionInput> customizedExecutionInputMono = executionInputCustomizer
				.customizeExecutionInput(executionInput, serverWebExchange);
		return customizedExecutionInputMono.flatMap(
				customizedExecutionInput -> Mono.fromCompletionStage(graphQL.executeAsync(customizedExecutionInput)));
	}

}
