# This project is archived in favor of the offical [Spring GraphQL](https://github.com/spring-projects/spring-graphql) integration.

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

The following Beans can be overridden by providing a different implementation. 

| Interface | Description | 
| --- | --- | 
| GraphQLInvocation | Executes one request. The default impl just calls the provided `GraphQL` bean.|
| ExecutionResultHandler | Takes a `ExecutionResult` and sends the result back to the client. The default impl returns `ExecutionResult.toSpecification()` as json. |




