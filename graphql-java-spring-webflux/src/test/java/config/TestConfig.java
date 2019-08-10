package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.spring.web.reactive.ExecutionInputMapper;
import graphql.spring.web.reactive.GraphqlHandler;
import graphql.spring.web.reactive.config.BeanNames;
import graphql.spring.web.reactive.config.EnableGraphql;
import graphql.spring.web.reactive.config.GraphqlConfigurer;
import graphql.spring.web.reactive.impl.ExecutionInputMapperImpl;
import graphql.spring.web.reactive.impl.GraphqlHandlerImpl;
import org.dataloader.DataLoaderRegistry;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableGraphql
@EnableWebFlux
@Configuration
public class TestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(BeanNames.GRAPHQL)
    public GraphQL graphQL() {
        return Mockito.mock(GraphQL.class);
    }

    @Bean(BeanNames.DATA_LOADER_REGISTRY)
    public DataLoaderRegistry dataLoaderRegistry() {
        return new DataLoaderRegistry();
    }

    @Bean(BeanNames.EXECUTION_INPUT_MAPPER)
    public ExecutionInputMapper executionInputMapper() {
        return new ExecutionInputMapperImpl(dataLoaderRegistry());
    }

    @Bean(BeanNames.HANDLER)
    public GraphqlHandler graphqlHandler() {
        return new GraphqlHandlerImpl(graphQL(), objectMapper(), executionInputMapper());
    }

    @Bean(BeanNames.CONFIGURER)
    public GraphqlConfigurer graphqlConfigurer(Environment environment) {
        return new GraphqlConfigurer() {
            @Override
            public String getHttpEndpoint() {
                return environment.getProperty("graphql.url", "/graphql");
            }

            @Override
            public String getWebsocketEndpoint() {
                return null;
            }
        };
    }
}