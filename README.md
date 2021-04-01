# GraphQL Java Spring

## Status


Version 2.0 is released.

We have a [spectrum chat](https://spectrum.chat/graphql-java) for general questions.


## Overview

This project integrates [GraphQL Java](https://github.com/graphql-java/graphql-java) into Spring/Spring Boot, by enabling query execution via HTTP.

While the GraphQL Specification itself doesn't specify any transport protocol there is a quasi standard how to do it described 
[here](https://graphql.org/learn/serving-over-http/) and this project follows this quasi standard.

Goals / Design:

- Just HTTP JSON: the current focus is on HTTP execution via JSON.
- Minimal Dependencies: the only dependencies are GraphQL Java and Spring projects (including Jackson for JSON handling).
- No additional abstraction layer on top of GraphQL Java: GraphQL Java is meant to be used directly. 


## Supported HTTP Requests

As outlined in https://graphql.org/learn/serving-over-http this project supports:

1. GET request with `query`, `operationName` and `variables` parameters. The variable parameters are json encoded
2. POST request with body `application/json` and keys `query` (string), `operationName` (string) and `variables` (map).

Both produce `application/json`.

## Support for webmvc and webflux

We support both spring web types: the fully non-blocking `webflux` and the traditional servlet based `webmvc`.

Please see [here](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-framework-choice) in 
the spring documentation itself about the differences.



## Artifacts

There are four different artifacts: (all with group id `com.graphql-java`)

1. `graphql-java-spring-webflux`
2. `graphql-java-spring-boot-starter-webflux`
3. `graphql-java-spring-webmvc`
4. `graphql-java-spring-boot-starter-webmvc`



## Getting started with Spring Boot (webflux and webmvc)

The Spring Boot Starter artifact provides a HTTP endpoint on `${graphql.url}` with the default value `"/graphql"` just by being on the classpath.

The only requirement is to have a Bean of type `graphql.GraphQL` available.

Add the following dependency to your `build.gradle` (make sure `mavenCentral()` is among your repos)

for webflux:
```groovy
dependencies {
    implementation "com.graphql-java:graphql-java-spring-boot-starter-webflux:2.0"
}
```

for webmvc:
```groovy
dependencies {
    implementation "com.graphql-java:graphql-java-spring-boot-starter-webmvc:2.0"
}
```

or to your `pom.xml`

for webflux
```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java-spring-boot-starter-webflux</artifactId>
    <version>2.0</version>
</dependency>

```

for webmvc:
```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java-spring-boot-starter-webmvc</artifactId>
    <version>2.0</version>
</dependency>

```

## Ways to customize   


### Properties

The following properties are currently available:

| Property | Description | Default Value |
| --- | --- | --- |
| graphql.url | the endpoint url | graphql |


### Beans

Several Beans can be overriden by providing a different implementation. They are in the `graphql.spring.web.reactive.components` or `graphql.spring.web.servlet.components` package, depending on whether you choose the `spring-webflux` or the `spring-webmvc` depedency.

Amongs them are:

| Interface | Description | 
| --- | --- | 
| GraphQLInvocation | Executes one request. The default impl just calls the provided `GraphQL` bean.|
| ExecutionResultHandler | Takes a `ExecutionResult` and sends the result back to the client. The default impl returns `ExecutionResult.toSpecification()` as json. |

### DataLoader

The _DefaultGraphQLInvocation_ bean looks for these beans:

* if an `OnDemandDataLoaderRegistry` Spring Bean is found, then its `getNewDataLoaderRegistry()` method is called for each request invocation. This allows to have one `DataLoderRegistry` per request, and so, a _per request_ cache.

* else, if a `DataLoderRegistry` Spring Bean is found, then it is used for every requests. Its up to this bean to properly defined the caching strategy.

* else no DataLoader is used.
