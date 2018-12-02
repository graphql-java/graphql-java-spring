package graphql.spring.web.reactive;

import graphql.PublicApi;
import org.springframework.web.server.ServerWebExchange;

@PublicApi
public interface GraphQLContextBuilder {

    Object build(ServerWebExchange webRequest);
}
