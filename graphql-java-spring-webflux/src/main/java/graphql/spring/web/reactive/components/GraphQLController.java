package graphql.spring.web.reactive.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.Internal;
import graphql.spring.web.reactive.ExecutionResultHandler;
import graphql.spring.web.reactive.GraphQLInvocation;
import graphql.spring.web.reactive.GraphQLInvocationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@Internal
public class GraphQLController {

    @Autowired
    GraphQLInvocation graphQLInvocation;

    @Autowired
    ExecutionResultHandler executionResultHandler;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "${graphql.url:graphql}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object graphqlPOST(
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "operationName", required = false) String operationName,
            @RequestParam(value = "variables", required = false) String variablesJson,
            @RequestBody(required = false) String body,
            ServerWebExchange serverWebExchange) throws IOException {

        if (body == null) {
            body = "";
        }

        // https://graphql.org/learn/serving-over-http/#post-request
        //
        // A standard GraphQL POST request should use the application/json content type,
        // and include a JSON-encoded body of the following form:
        //
        // {
        //   "query": "...",
        //   "operationName": "...",
        //   "variables": { "myVariable": "someValue", ... }
        // }

        if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            GraphQLRequestBody request = objectMapper.readValue(body, GraphQLRequestBody.class);
            if (request.getQuery() == null) {
                request.setQuery("");
            }
            return executeRequest(request.getQuery(), request.getOperationName(), request.getVariables(), serverWebExchange);
        }

        // In addition to the above, we recommend supporting two additional cases:
        //
        // * If the "query" query string parameter is present (as in the GET example above),
        //   it should be parsed and handled in the same way as the HTTP GET case.

        if (query != null) {
            return executeRequest(query, operationName, convertVariablesJson(variablesJson), serverWebExchange);
        }

        // * If the "application/graphql" Content-Type header is present,
        //   treat the HTTP POST body contents as the GraphQL query string.

        if ("application/graphql".equals(contentType)) {
            return executeRequest(body, null, null, serverWebExchange);
        }

        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
    }

    @RequestMapping(value = "${graphql.url:graphql}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object graphqlGET(
            @RequestParam("query") String query,
            @RequestParam(value = "operationName", required = false) String operationName,
            @RequestParam(value = "variables", required = false) String variablesJson,
            ServerWebExchange serverWebExchange) {

        // https://graphql.org/learn/serving-over-http/#get-request
        //
        // When receiving an HTTP GET request, the GraphQL query should be specified in the "query" query string.
        // For example, if we wanted to execute the following GraphQL query:
        //
        // {
        //   me {
        //     name
        //   }
        // }
        //
        // This request could be sent via an HTTP GET like so:
        //
        // http://myapi/graphql?query={me{name}}
        //
        // Query variables can be sent as a JSON-encoded string in an additional query parameter called "variables".
        // If the query contains several named operations,
        // an "operationName" query parameter can be used to control which one should be executed.

        return executeRequest(query, operationName, convertVariablesJson(variablesJson), serverWebExchange);
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null) return Collections.emptyMap();
        try {
            return objectMapper.readValue(jsonMap, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert variables GET parameter: expected a JSON map", e);
        }

    }

    private Object executeRequest(
            String query,
            String operationName,
            Map<String, Object> variables,
            ServerWebExchange serverWebExchange) {
        GraphQLInvocationData invocationData = new GraphQLInvocationData(query, operationName, variables);
        Mono<ExecutionResult> executionResult = graphQLInvocation.invoke(invocationData, serverWebExchange);
        return executionResultHandler.handleExecutionResult(executionResult, serverWebExchange.getResponse());
    }

}
