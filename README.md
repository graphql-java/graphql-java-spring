# THIS IS STILL WIP AND NOT RELEASED YET
# GraphQL Java Spring


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

## Spring Boot Starter

The Spring Boot Starter artifact provides a HTTP endpoint on ${graphql.url} with the default value "/graphql" just by being on the classpath.

The only requirement is to have a Bean of type `graphql.GraphQL` available.

Add the following dependency to your `build.gradle` (make sure `mavenCentral()` is among your repos)

```groovy
dependencies {
    implementation "com.graphql-java:graphql-java-spring-boot-starter:1.0"
}

```

or to your `pom.xml`

```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>

```

## Spring Web Artifact

We also provide a artifact for non Boot Application. 


