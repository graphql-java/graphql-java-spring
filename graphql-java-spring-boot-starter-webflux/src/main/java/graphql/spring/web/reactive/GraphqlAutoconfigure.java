package graphql.spring.web.reactive;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.spring.web.reactive.config.EnableGraphql;
import graphql.spring.web.reactive.config.GraphqlConfigurer;
import graphql.spring.web.reactive.impl.ExecutionInputMapperImpl;
import graphql.spring.web.reactive.impl.GraphqlHandlerImpl;
import org.dataloader.DataLoaderRegistry;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static graphql.spring.web.reactive.config.BeanNames.*;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Graphql.
 */
@EnableGraphql
@EnableConfigurationProperties(GraphqlProperties.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
@Configuration(proxyBeanMethods = false)
public class GraphqlAutoconfigure {

    private final ApplicationContext context;

    private final GraphqlProperties properties;

    public GraphqlAutoconfigure(ApplicationContext context, GraphqlProperties properties) {
        this.context = context;
        this.properties = properties;
    }

    @Bean(DATA_LOADER_REGISTRY)
    @ConditionalOnMissingBean
    public DataLoaderRegistry dataLoaderRegistry() {
        return new DataLoaderRegistry();
    }

    @Bean(EXECUTION_INPUT_MAPPER)
    @ConditionalOnMissingBean
    public ExecutionInputMapper executionInputMapper() {
        DataLoaderRegistry dataLoaderRegistry = context.getBean(DATA_LOADER_REGISTRY, DataLoaderRegistry.class);
        return new ExecutionInputMapperImpl(dataLoaderRegistry);
    }

    @Bean(HANDLER)
    @ConditionalOnMissingBean
    public GraphqlHandler graphqlHandler() {
        GraphQL graphql = context.getBean(GRAPHQL, GraphQL.class);
        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
        ExecutionInputMapper executionInputMapper = context.getBean(EXECUTION_INPUT_MAPPER, ExecutionInputMapper.class);
        return new GraphqlHandlerImpl(graphql, objectMapper, executionInputMapper);
    }

    @Bean(CONFIGURER)
    @ConditionalOnMissingBean
    public GraphqlConfigurer graphqlConfigurer() {
        return new GraphqlConfigurer() {

            @Override
            public String getHttpEndpoint() {
                return properties.getHttpEndpoint();
            }

            @Override
            public String getWebsocketEndpoint() {
                return properties.getWebsocketEndpoint();
            }
        };
    }
}