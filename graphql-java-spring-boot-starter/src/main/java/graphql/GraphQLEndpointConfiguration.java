package graphql;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnWebApplication
@ComponentScan("graphql")
public class GraphQLEndpointConfiguration {


    @PostConstruct
    public void init() {
    }
}
