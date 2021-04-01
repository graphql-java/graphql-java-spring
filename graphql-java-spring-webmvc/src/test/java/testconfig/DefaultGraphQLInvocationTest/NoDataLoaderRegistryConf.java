package testconfig.DefaultGraphQLInvocationTest;

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
import graphql.spring.web.servlet.components.DefaultGraphQLInvocationTest;
import graphql.spring.web.servlet.components.GraphQLController;

/**
 * Spring {@link Configuration} file for the {@link DefaultGraphQLInvocationTest#testNoDataLoader()} test.
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackageClasses = GraphQLController.class)
public class NoDataLoaderRegistryConf {

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
