package testconfig.DefaultGraphQLInvocationTest;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.spring.web.reactive.OnDemandDataLoaderRegistry;
import graphql.spring.web.reactive.components.DefaultGraphQLInvocationTest;
import graphql.spring.web.reactive.components.GraphQLController;

/**
 * Spring {@link Configuration} file for the {@link DefaultGraphQLInvocationTest#testOnDemandeDataLoader()} test.
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackageClasses = GraphQLController.class)
public class OnDemandDataLoaderRegistryConf {

	@Bean
	public OnDemandDataLoaderRegistry onDemandDataLoaderRegistry() {
		return new OnDemandDataLoaderRegistry() {
			@Override
			public DataLoaderRegistry getNewDataLoaderRegistry() {
				DataLoaderRegistry registry = new DataLoaderRegistry();
				registry.register(DefaultGraphQLInvocationTest.DATA_LOADER_NAME,
						DataLoader.newDataLoader(new BatchLoaderImpl()));
				return registry;
			}
		};
	}

	// This beans traps the {@link DataLoaderRegistry} at each request invocation
	@Bean
	@Primary
	TestExecutionInputCustomizer testExecutionInputCustomizer() {
		return new TestExecutionInputCustomizer();
	}

	@Bean
	ObjectMapper objectMapper() {
		// It won't be used
		return new ObjectMapper();
	}

	@Bean
	public GraphQL graphQL() {
		String schema = "type Query{helloWorld: String}";
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);
		RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().build();
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

		return GraphQL.newGraphQL(graphQLSchema).build();
	}
}
