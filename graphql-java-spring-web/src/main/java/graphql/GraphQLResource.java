package graphql;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class GraphQLResource {

    @Autowired
    private GraphQL graphql;


    @RequestMapping(value = "/graphql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> graphql(@RequestBody Map<String, Object> body) {
        String query = (String) body.get("query");
        if (query == null) {
            query = "";
        }
        String operationName = (String) body.get("operationName");
        Map<String, Object> variables = (Map<String, Object>) body.get("variables");
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }
        return executeGraphqlQuery(query, operationName, variables);
    }

    private Map<String, Object> executeGraphqlQuery(String query, String operationName, Map<String, Object> variables) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .operationName(operationName)
                .variables(variables)
                .build();
        return this.graphql.execute(executionInput).toSpecification();
    }

}
