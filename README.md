# GraphQL Java Spring


This project integrates GraphQL Java into Spring/Spring Boot, by enabling query execution via HTTP.

While the GraphQL Specification itself doesn't specify any transport protocol there is a quasi standard how to do it described 
[here](https://graphql.org/learn/serving-over-http/) and this project follows this quasi standard.

Goals / Design:

- Just HTTP JSON: the current focus is on HTTP execution via JSON.
- Minimal Dependencies: the only dependencies are GraphQL Java and Spring projects.
- No additional abstraction layer on top of GraphQL Java: GraphQL Java is meant to be used directly. 



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
