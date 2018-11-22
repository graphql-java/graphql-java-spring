package graphql.spring.web.servlet.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = GraphQLController.class)
public class TestAppConfig {


    @Bean
    public GraphQL graphQL() {
        GraphQL graphql = Mockito.mock(GraphQL.class);
        return graphql;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
