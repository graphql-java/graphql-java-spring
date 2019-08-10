package graphql.spring.web.servlet.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Adding this annotation to an {@code @Configuration} class imports
 * the graphql configuration from {@link GraphqlConfiguration} that registers graphql's endpoints.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(GraphqlConfiguration.class)
public @interface EnableGraphql {

}