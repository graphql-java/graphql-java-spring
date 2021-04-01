package graphql.spring.web.reactive.components;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.reactive.ExecutionInputCustomizer;
import graphql.spring.web.reactive.GraphQLInvocationData;

import org.dataloader.DataLoader;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import testconfig.DefaultGraphQLInvocationTest.NoDataLoaderRegistryConf;
import testconfig.DefaultGraphQLInvocationTest.OnDemandDataLoaderRegistryConf;
import testconfig.DefaultGraphQLInvocationTest.TestExecutionInputCustomizer;
import testconfig.DefaultGraphQLInvocationTest.WithDataLoaderRegistryConf;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultGraphQLInvocationTest {

	public final static String DATA_LOADER_NAME = "TestDataLoader";
	public final static String DUMMY_ERROR_MESSAGE = "This is a dummy error";

	@Test
	public void testCustomizerIsCalled() {

		String query = "query myQuery {foo}";
		String operationName = "myQuery";
		Map<String, Object> variables = new LinkedHashMap<>();

		DefaultGraphQLInvocation defaultGraphQLInvocation = new DefaultGraphQLInvocation();
		ExecutionInputCustomizer executionInputCustomizer = mock(ExecutionInputCustomizer.class);
		defaultGraphQLInvocation.executionInputCustomizer = executionInputCustomizer;
		GraphQL graphQL = mock(GraphQL.class);
		defaultGraphQLInvocation.graphQL = graphQL;
		ExecutionResult executionResult = mock(ExecutionResult.class);
		when(graphQL.executeAsync(any(ExecutionInput.class))).thenReturn(completedFuture(executionResult));

		GraphQLInvocationData graphQLInvocationData = new GraphQLInvocationData(query, operationName, variables);
		ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);

		ArgumentCaptor<ExecutionInput> captor1 = ArgumentCaptor.forClass(ExecutionInput.class);
		ArgumentCaptor<ServerWebExchange> captor2 = ArgumentCaptor.forClass(ServerWebExchange.class);
		ExecutionInput executionInputResult = mock(ExecutionInput.class);
		when(executionInputCustomizer.customizeExecutionInput(captor1.capture(), captor2.capture()))
				.thenReturn(Mono.just(executionInputResult));

		Mono<ExecutionResult> invoke = defaultGraphQLInvocation.invoke(graphQLInvocationData, serverWebExchange);

		assertThat(captor1.getValue().getQuery()).isEqualTo(query);
		assertThat(captor1.getValue().getOperationName()).isEqualTo(operationName);
		assertThat(captor1.getValue().getVariables()).isSameAs(variables);

		invoke.block();

		verify(graphQL).executeAsync(executionInputResult);

	}

	@Test
	public void testNoDataLoader() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(NoDataLoaderRegistryConf.class);

		DataLoader<?, ?> dl = invokeRequestAndGetDataLoader(ctx);
		assertNull("no data loader should exist in the request context", dl);
	}

	@Test
	public void testWithDataLoader() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(WithDataLoaderRegistryConf.class);

		DataLoader<?, ?> dl1 = invokeRequestAndGetDataLoader(ctx);
		DataLoader<?, ?> dl2 = invokeRequestAndGetDataLoader(ctx);

		assertNotNull("A data loader should exist in the request context (1)", dl1);
		assertNotNull("A data loader should exist in the request context (2)", dl2);
		assertEquals("The DataLoader should be the same one accross two request invocations", dl1, dl2);
	}

	@Test
	public void testOnDemandeDataLoader() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(OnDemandDataLoaderRegistryConf.class);

		DataLoader<?, ?> dl1 = invokeRequestAndGetDataLoader(ctx);
		DataLoader<?, ?> dl2 = invokeRequestAndGetDataLoader(ctx);

		assertNotNull("A data loader should exist in the request context (1)", dl1);
		assertNotNull("A data loader should exist in the request context (2)", dl2);
		assertNotEquals("The DataLoader should NOT be the same one accross two request invocations", dl1, dl2);
	}

	private DataLoader<?, ?> invokeRequestAndGetDataLoader(ApplicationContext ctx) {
		DefaultGraphQLInvocation defaultGraphQLInvocation = ctx.getBean(DefaultGraphQLInvocation.class);
		assertNotNull(defaultGraphQLInvocation);

		TestExecutionInputCustomizer executionInputCustomizer = ctx.getBean(TestExecutionInputCustomizer.class);

		try {
			String query = "query{helloWorld}";
			String operationName = "query";
			defaultGraphQLInvocation.invoke(new GraphQLInvocationData(query, operationName, null), null);
			fail("This text expects an exception");
			// The next line will never be executed. But it is needed, as it is needed to avoid compilation error.
			return null;
		} catch (RuntimeException e) {
			assertEquals(DUMMY_ERROR_MESSAGE, e.getMessage());
			return (executionInputCustomizer.lastReadDataLoaderRegistry == null) ? null
					: executionInputCustomizer.lastReadDataLoaderRegistry.getDataLoader(DATA_LOADER_NAME);
		}
	}

}