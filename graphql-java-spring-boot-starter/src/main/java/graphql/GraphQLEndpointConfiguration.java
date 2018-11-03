package graphql;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@ComponentScan("graphql")
public class GraphQLEndpointConfiguration {


}
