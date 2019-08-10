package graphql.spring.web.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import graphql.spring.web.servlet.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
public class GraphqlControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private GraphQL graphql;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    private String toJson(Map<String, Object> input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPostRequest() throws Exception {
        Map<String, Object> request = new LinkedHashMap<>();
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("variable", "variableValue");
        String query = "query myQuery {foo}";
        request.put("query", query);
        request.put("variables", variables);
        String operationName = "myQuery";
        request.put("operationName", operationName);

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture<ExecutionResult> cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);


        mockMvc.perform(post("/graphql")
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("data", is("bar")))
                .andReturn();

        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getQuery(), is(query));
        assertThat(captor.getValue().getVariables(), is(variables));
        assertThat(captor.getValue().getOperationName(), is(operationName));

    }

    @Test
    public void testSimplePostRequest() throws Exception {
        Map<String, Object> request = new LinkedHashMap<>();
        String query = "{foo}";
        request.put("query", query);

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture<ExecutionResult> cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);


        mockMvc.perform(post("/graphql")
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", is("bar")))
                .andReturn();

        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getQuery(), is(query));
    }

    @Test
    public void testQueryParamPostRequest() throws Exception {
        String variablesJson = "{\"variable\":\"variableValue\"}";
        String query = "query myQuery {foo}";
        String operationName = "myQuery";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture<ExecutionResult> cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);


        mockMvc.perform(post("/graphql")
                .param("query", query)
                .param("variables", variablesJson)
                .param("operationName", operationName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", is("bar")))
                .andReturn();

        assertThat(captor.getAllValues().size(), is(1));

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("variable", "variableValue");
        assertThat(captor.getValue().getQuery(), is(query));
        assertThat(captor.getValue().getVariables(), is(variables));
        assertThat(captor.getValue().getOperationName(), is(operationName));
    }

    @Test
    public void testSimpleQueryParamPostRequest() throws Exception {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture<ExecutionResult> cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);


        mockMvc.perform(post("/graphql")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", is("bar")))
                .andReturn();

        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getQuery(), is(query));
    }

    @Test
    public void testApplicationGraphqlPostRequest() throws Exception {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture<ExecutionResult> cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        mockMvc.perform(post("/graphql")
                .content(query)
                .contentType("application/graphql"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", is("bar")))
                .andReturn();

        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getQuery(), is(query));
    }

    @Test
    public void testGetRequest() throws Exception {
        String variablesJson = "{\"variable\":\"variableValue\"}";
        String query = "query myQuery {foo}";
        String operationName = "myQuery";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture<ExecutionResult> cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);


        mockMvc.perform(get("/graphql")
                .param("query", query)
                .param("variables", variablesJson)
                .param("operationName", operationName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", is("bar")))
                .andReturn();

        assertThat(captor.getAllValues().size(), is(1));

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("variable", "variableValue");
        assertThat(captor.getValue().getQuery(), is(query));
        assertThat(captor.getValue().getVariables(), is(variables));
        assertThat(captor.getValue().getOperationName(), is(operationName));

    }

    @Test
    public void testSimpleGetRequest() throws Exception {
        String query = "{foo}";

        ExecutionResultImpl executionResult = ExecutionResultImpl.newExecutionResult()
                .data("bar")
                .build();
        CompletableFuture<ExecutionResult> cf = CompletableFuture.completedFuture(executionResult);
        ArgumentCaptor<ExecutionInput> captor = ArgumentCaptor.forClass(ExecutionInput.class);
        Mockito.when(graphql.executeAsync(captor.capture())).thenReturn(cf);

        mockMvc.perform(get("/graphql")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", is("bar")))
                .andReturn();

        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getQuery(), is(query));
    }
}