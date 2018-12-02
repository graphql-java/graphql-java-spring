package graphql.spring.web.servlet;

import graphql.PublicApi;
import org.springframework.web.context.request.WebRequest;

@PublicApi
public interface GraphQLContextBuilder {

    Object build(WebRequest webRequest);
}
