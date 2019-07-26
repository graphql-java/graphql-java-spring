package graphql.spring.web.servlet.config;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.spring.web.servlet.GraphqlHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Objects;

import static org.springframework.web.servlet.function.RequestPredicates.contentType;
import static org.springframework.web.servlet.function.RequestPredicates.param;

/**
 * Graphql configuration
 */
@Configuration(proxyBeanMethods = false)
public class GraphqlConfiguration {

    private final ApplicationContext context;

    public GraphqlConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Bean(BeanNames.GRAPHQL)
    public GraphQL graphql() {
        GraphqlCustomizer customizer = context.getBeanProvider(GraphqlCustomizer.class).getIfUnique();
        GraphQL.Builder graphql = GraphQL.newGraphQL(context.getBean(BeanNames.SCHEMA, GraphQLSchema.class));
        if (customizer != null) {
            return customizer.customize(graphql);
        }
        return graphql.build();
    }

    @Bean(BeanNames.ROUTER_FUNCTION)
    public RouterFunction<ServerResponse> routerFunction() {
        GraphqlHandler handler = context.getBean(BeanNames.HANDLER, GraphqlHandler.class);
        String pattern = context.getBean(GraphqlConfigurer.class).getHttpEndpoint();
        return RouterFunctions.route()
                .GET(pattern, handler::invokeByParams)
                .POST(pattern, param("query", Objects::nonNull), handler::invokeByParams)
                .POST(pattern, contentType(MediaType.APPLICATION_JSON), handler::invokeByBody)
                .POST(pattern, contentType(new MediaType("application", "graphql")), handler::invokeByParamsAndBody)
                .build();
    }
}