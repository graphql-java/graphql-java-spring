package graphql.spring.web.reactive.config;

import graphql.GraphQL;

/**
 * Callback interface that can be implemented by beans wishing to customize the graphql
 * configuration.
 */
public interface GraphqlCustomizer {

    /**
     * Customize the graphql configuration.
     *
     * @param builder graphql builder
     */
    GraphQL customize(GraphQL.Builder builder);
}