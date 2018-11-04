package graphql.spring.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.Internal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Map;

@RestController
@Internal
public class GraphQLController {

    @Autowired
    private GraphQLInvocation graphQLInvocation;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "${graphql.url:graphql}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object graphqlPOST(@RequestBody GraphQLRequestBody body, WebRequest webRequest) {
        String query = body.getQuery();
        if (query == null) {
            query = "";
        }
        return graphQLInvocation.invoke(new GraphQLInvocationData(query, body.getOperationName(), body.getVariables()), webRequest);
    }

    @RequestMapping(value = "${graphql.url:graphql}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object graphqlGET(
            @RequestParam("query") String query,
            @RequestParam(value = "operationName", required = false) String operationName,
            @RequestParam(value = "variables", required = false) String variablesJson,
            WebRequest webRequest) {
        return graphQLInvocation.invoke(new GraphQLInvocationData(query, operationName, convertVariablesJson(variablesJson)), webRequest);
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        try {
            return objectMapper.readValue(jsonMap, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert variables GET parameter: expected a JSON map", e);
        }

    }


}
