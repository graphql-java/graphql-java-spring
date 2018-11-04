package graphql.spring.controller;

import graphql.PublicApi;
import org.springframework.web.context.request.WebRequest;

@PublicApi
public interface GraphQLInvocation {

    Object invoke(GraphQLInvocationData invocationData, WebRequest webRequest);

}
