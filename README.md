# Spring Jdbc Oracle

This repo references from [Spring Data JDBC Extensions for the Oracle database](https://github.com/spring-attic/spring-data-jdbc-ext).
I just copy and modify some code that I think it is necessary for me.

## I. Before You Start

I tested on below Software/Framework version and I am **NOT** sure it can work on another platform.

| **Software**     | **Version** |
|------------------|-------------|
| Java             | 17          |
| Maven            | 3.9.8       |


| **Framework**    | **Version**  |
|------------------|--------------|
| Spring Framework | 6.1.10       |
| Spring Boot      | 3.3.1        |


## II. How to test

### 1. Use testcontainer

1.1. Open cmd and type

 ```cmd
 spring-jdbc-oracle> docker compose up
 ```
1.2. After docker runs finish, you just run `ExampleServiceContainerTest.java`

### 2. Use `docker-compose.yml` 

1. You just run `ExampleServiceTest.java`