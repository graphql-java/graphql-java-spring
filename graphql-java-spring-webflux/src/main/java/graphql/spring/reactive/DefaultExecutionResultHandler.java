package graphql.spring.reactive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.DeferredExecutionResult;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Autowired
    ObjectMapper objectMapper;

    private static final String CRLF = "\r\n";

    @Override
    public Object handleExecutionResult(Mono<ExecutionResult> executionResultMono, ServerHttpResponse serverHttpResponse) {
        return executionResultMono.map(executionResult -> handleImpl(executionResult, serverHttpResponse));
    }

    private Object handleImpl(ExecutionResult executionResult, ServerHttpResponse serverHttpResponse) {
        Map<Object, Object> extensions = executionResult.getExtensions();
        if (extensions != null && extensions.containsKey(GraphQL.DEFERRED_RESULTS)) {
            return handleDeferResponse(serverHttpResponse, executionResult, extensions);
        } else {
            return executionResult.toSpecification();
        }
    }

    private Mono<Void> handleDeferResponse(ServerHttpResponse serverHttpResponse,
                                           ExecutionResult executionResult,
                                           Map<Object, Object> extensions) {
        Publisher<DeferredExecutionResult> deferredResults = (Publisher<DeferredExecutionResult>) extensions.get(GraphQL.DEFERRED_RESULTS);
        // this implements this apollo defer spec: https://github.com/apollographql/apollo-server/blob/defer-support/docs/source/defer-support.md
        // the spec says CRLF + "-----" + CRLF is needed at the end, but it works without it and with it we get client
        // side errors with it, so we skp it
        serverHttpResponse.setStatusCode(HttpStatus.OK);
        HttpHeaders headers = serverHttpResponse.getHeaders();
        headers.set("Content-Type", "multipart/mixed; boundary=\"-\"");
        headers.set("Connection", "keep-alive");

        Flux<Mono<DataBuffer>> deferredDataBuffers = Flux.from(deferredResults).map(deferredExecutionResult -> {
            DeferPart deferPart = new DeferPart(deferredExecutionResult.toSpecification());
            StringBuilder builder = new StringBuilder();
            String body = deferPart.write();
            builder.append(CRLF).append("---").append(CRLF);
            builder.append(body);
            return strToDataBuffer(builder.toString());
        });
        Flux<Mono<DataBuffer>> firstResult = Flux.just(firstResult(executionResult));


        return serverHttpResponse.writeAndFlushWith(Flux.mergeSequential(firstResult, deferredDataBuffers));
    }

    private Mono<DataBuffer> firstResult(ExecutionResult executionResult) {
        StringBuilder builder = new StringBuilder();
        builder.append(CRLF).append("---").append(CRLF);
        DeferPart deferPart = new DeferPart(executionResult.toSpecification());
        String body = deferPart.write();
        builder.append(body);
        Mono<DataBuffer> dataBufferMono = strToDataBuffer(body);
        return dataBufferMono;
    }

    private Mono<DataBuffer> strToDataBuffer(String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        DefaultDataBufferFactory defaultDataBufferFactory = new DefaultDataBufferFactory();
        return Mono.just(defaultDataBufferFactory.wrap(bytes));
    }

    private class DeferPart {

        private Object body;

        public DeferPart(Object data) {
            this.body = data;
        }

        public String write() {
            StringBuilder result = new StringBuilder();
            String bodyString = bodyToString();
            result.append("Content-Type: application/json").append(CRLF);
            result.append("Content-Length: ").append(bodyString.length()).append(CRLF).append(CRLF);
            result.append(bodyString);
            return result.toString();
        }

        private String bodyToString() {
            try {
                return objectMapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
