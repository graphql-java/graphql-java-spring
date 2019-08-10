package graphql.spring.web.servlet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import graphql.Assert;
import graphql.PublicApi;
import graphql.spring.web.servlet.jackson.JacksonVariableDeserializer;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

@PublicApi
public class GraphQLInvocationData {

    private final String query;

    private String operationName;

    @JsonDeserialize(using = JacksonVariableDeserializer.class)
    private Map<String, Object> variables = new HashMap<>();

    @JsonCreator
    public GraphQLInvocationData(@JsonProperty("query") String query) {
        this.query = Assert.assertNotNull(query, "query must be provided");
    }

    public String getQuery() {
        return query;
    }

    @Nullable
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(@Nullable String operationName) {
        this.operationName = operationName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}