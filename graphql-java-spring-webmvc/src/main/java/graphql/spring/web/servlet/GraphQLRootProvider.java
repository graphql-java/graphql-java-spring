package graphql.spring.web.servlet;

import java.util.function.Supplier;

import graphql.PublicSpi;

@PublicSpi
public interface GraphQLRootProvider extends Supplier<Object> {

	@Override
	public Object get();
	
}
