package graphql.spring.web.servlet.components;

import graphql.ExecutionResult;
import graphql.Internal;
import graphql.spring.web.servlet.ExecutionResultHandler;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import graphql.spring.web.servlet.JsonSerializer;
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
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@Internal
public class GraphQLController {

    @Autowired
    GraphQLInvocation graphQLInvocation;

    @Autowired
    ExecutionResultHandler executionResultHandler;

    @Autowired
    JsonSerializer jsonSerializer;

    @RequestMapping(value = "${graphql.url:graphql}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object graphqlPOST(
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "operationName", required = false) String operationName,
            @RequestParam(value = "variables", required = false) String variablesJson,
            @RequestBody(required = false) String body,
            WebRequest webRequest) throws IOException, OperationNotSupportedException {

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
            GraphQLRequestBody request = jsonSerializer.deserialize(body, GraphQLRequestBody.class);
            if (request.getQuery() == null) {
                request.setQuery("");
            }
            return executeRequest(request.getQuery(), request.getOperationName(), request.getVariables(), webRequest);
        }else if(contentType!=null && contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)){
            // In this case body is not mapped, same as all on @RequestParams.
            // Because we send files as variables we just have to take care of variables part of deserialized GraphQLRequestBody.
            // "map" parameter tells us how to link multiPartFiles to their variables.
            // Map can look like this:
            // {
            // "0" : ["variables.files"],
            // "1" : ["variables.filesList.0"],
            // "2" : ["variables.filesList.1"]
            // }
            // It tells us that multipartFile
            // that has a key "0" should be linked to variable named file, and multipartFile with a key "1" should be put as 0th indexed element
            // in list named filesList in variables and "2" as a 1st element in the same list.
            GraphQLRequestBody request = jsonSerializer.deserialize(webRequest.getParameter("operations"),GraphQLRequestBody.class);
            if(request.getQuery() != null) {
                LinkedHashMap<String, ArrayList<String>> multipartFileKeyVariablePathMap = this.jsonSerializer.deserialize(webRequest.getParameter("map"), LinkedHashMap.class);

                StandardMultipartHttpServletRequest multiPartRequest = (StandardMultipartHttpServletRequest) ((ServletWebRequest) webRequest).getNativeRequest();
                Map<String, MultipartFile> multipartFileMap = multiPartRequest.getFileMap();

                for(Map.Entry<String,MultipartFile> e: multipartFileMap.entrySet()){
                    String pathString = multipartFileKeyVariablePathMap.get(e.getKey()).get(0); /*i.e. "variables.files" or "variables.fileList.NUMBER*/
                    if (pathString.matches("variables\\.[a-zA-Z0-9]*?\\.\\d")) {
                        String[] splittedPath = pathString.split("\\.", 3);
                        final Object variablesArray = request.getVariables().get(splittedPath[1]);
                        if (variablesArray instanceof ArrayList)
                            ((ArrayList) variablesArray).set(Integer.parseInt(splittedPath[2]), e.getValue());
                        else
                            throw new OperationNotSupportedException("Array of files represented by not supported collection");
                    } else if (pathString.startsWith("variables.")) {
                        String[] splittedPath = pathString.split("\\.",2);
                        request.getVariables().put(splittedPath[1],e.getValue());
                    }
                }
            }else {
                request.setQuery("");
            }

            return executeRequest(request.getQuery(),request.getOperationName(),request.getVariables(),webRequest);
        }

        // In addition to the above, we recommend supporting two additional cases:
        //
        // * If the "query" query string parameter is present (as in the GET example above),
        //   it should be parsed and handled in the same way as the HTTP GET case.

        if (query != null) {
            return executeRequest(query, operationName, convertVariablesJson(variablesJson), webRequest);
        }

        // * If the "application/graphql" Content-Type header is present,
        //   treat the HTTP POST body contents as the GraphQL query string.

        if ("application/graphql".equals(contentType)) {
            return executeRequest(body, null, null, webRequest);
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
            WebRequest webRequest) {

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

        return executeRequest(query, operationName, convertVariablesJson(variablesJson), webRequest);
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null) {
            return Collections.emptyMap();
        }
        return jsonSerializer.deserialize(jsonMap, Map.class);
    }

    private Object executeRequest(
            String query,
            String operationName,
            Map<String, Object> variables,
            WebRequest webRequest) {
        GraphQLInvocationData invocationData = new GraphQLInvocationData(query, operationName, variables);
        CompletableFuture<ExecutionResult> executionResult = graphQLInvocation.invoke(invocationData, webRequest);
        return executionResultHandler.handleExecutionResult(executionResult);
    }

}
