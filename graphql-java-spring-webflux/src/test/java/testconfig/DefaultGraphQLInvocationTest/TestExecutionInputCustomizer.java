package testconfig.DefaultGraphQLInvocationTest;

import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import graphql.ExecutionInput;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.components.DefaultGraphQLInvocationTest;
import reactor.core.publisher.Mono;

/**
 * This Test class allows to catch the DataLoaderRegistry that is defined in the request context (if any) at each
 * request invocation.<BR/>
 * It must be loaded as a Spring Bean, in the Spring context. But <U>only for the {@link DefaultGraphQLInvocationTest}
 * test class</U>. So it's NOT marked by the @{@link Component} annotation. It's loaded as a bean in the
 * {@link Configuration} classes of this package, to be reused by the various Spring {@link Configuration}s that is
 * contains.
 */
public class TestExecutionInputCustomizer implements ExecutionInputCustomizer {
	public DataLoaderRegistry lastReadDataLoaderRegistry;

	@Override
	public Mono<ExecutionInput> customizeExecutionInput(ExecutionInput executionInput, ServerWebExchange webRequest) {
		lastReadDataLoaderRegistry = executionInput.getDataLoaderRegistry();

		// The test context doesn't allow to execute a real request. And the job is done: we've caught tyhe
		// DataLoaderRegistry.
		// So let's manage our specific exception, to stop here.
		throw new RuntimeException(DefaultGraphQLInvocationTest.DUMMY_ERROR_MESSAGE);
	}

}