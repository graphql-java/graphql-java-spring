package graphql.spring.web.servlet;

import graphql.Scalars;
import graphql.schema.*;
import graphql.spring.web.servlet.config.BeanNames;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DefaultApplication {

    @Bean(BeanNames.SCHEMA)
    public GraphQLSchema graphqlSchema() {
        return GraphQLSchema.newSchema()
                .query(GraphQLObjectType.newObject()
                        .name("Query")
                        .field(GraphQLFieldDefinition.newFieldDefinition()
                                .name("foo")
                                .type(Scalars.GraphQLString)
                                .build())
                        .build())
                .codeRegistry(GraphQLCodeRegistry.newCodeRegistry()
                        .dataFetcher(FieldCoordinates.coordinates("Query", "foo"), (DataFetcher<Object>) environment -> "bar")
                        .build())
                .build();
    }
}