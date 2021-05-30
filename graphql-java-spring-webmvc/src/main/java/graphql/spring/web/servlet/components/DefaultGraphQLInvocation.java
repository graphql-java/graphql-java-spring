package graphql.spring.web.servlet.components;

import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.servlet.ExecutionInputCustomizer;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import graphql.spring.web.servlet.GraphQLLocalContextProvider;
import graphql.spring.web.servlet.GraphQLRootProvider;

@Component
@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

    @Autowired
    GraphQL graphQL;

    @Autowired(required = false)
    GraphQLRootProvider rootProvider;
    
    @Autowired(required = false)
    GraphQLLocalContextProvider localContextProvider;

    @Autowired(required = false)
    DataLoaderRegistry dataLoaderRegistry;

    @Autowired
    ExecutionInputCustomizer executionInputCustomizer;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
    	ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables());
    	if (rootProvider != null) {
    		Object root = rootProvider.get();
    		executionInputBuilder.root(root);
    	}
    	if (localContextProvider != null) {
    		Object localContext = localContextProvider.get();
    		executionInputBuilder.localContext(localContext);
    	}
    	if (dataLoaderRegistry != null) {
            executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        }
        ExecutionInput executionInput = executionInputBuilder.build();
        CompletableFuture<ExecutionInput> customizedExecutionInput = executionInputCustomizer.customizeExecutionInput(executionInput, webRequest);
        return customizedExecutionInput.thenCompose(graphQL::executeAsync);
    }

}
