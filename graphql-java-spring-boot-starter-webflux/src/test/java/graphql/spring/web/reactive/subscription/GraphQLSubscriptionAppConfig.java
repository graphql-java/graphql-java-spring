package graphql.spring.web.reactive.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@EnableGraphqlSubscriptionEndpoint
public class GraphQLSubscriptionAppConfig {

    @Bean
    GraphQL graphQL() {
        Reader streamReader = loadSchemaFile("hello.graphqls");
        RuntimeWiring wiring = RuntimeWiring
                .newRuntimeWiring()
                .type(newTypeWiring("Subscription")
                        .dataFetcher("hello", environment -> Flux.just("world").repeat()))
                .build();
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(new SchemaParser().parse(streamReader), wiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private Reader loadSchemaFile(String name) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        return new InputStreamReader(stream);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
