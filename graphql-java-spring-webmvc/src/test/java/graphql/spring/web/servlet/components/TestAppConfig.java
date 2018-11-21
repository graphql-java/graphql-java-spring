package graphql.spring.web.servlet.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.CompletableFuture;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = GraphQLController.class)
public class TestAppConfig {


    @Bean
    public GraphQL graphQL() {
        GraphQL graphql = Mockito.mock(GraphQL.class);
        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("foo")
                .build();
        CompletableFuture cf = CompletableFuture.completedFuture(executionResult);
        Mockito.when(graphql.executeAsync(ArgumentMatchers.<ExecutionInput>any())).thenReturn(cf);
        return graphql;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
