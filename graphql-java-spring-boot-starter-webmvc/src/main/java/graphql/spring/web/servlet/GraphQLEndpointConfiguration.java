package graphql.spring.web.servlet;

import graphql.spring.web.servlet.controller.GraphQLController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnWebApplication
@ComponentScan(basePackageClasses = GraphQLController.class)
public class GraphQLEndpointConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
    }
}
