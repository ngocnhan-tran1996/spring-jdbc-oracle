# Spring Jdbc Oracle

This repo references from [Spring Data JDBC Extensions for the Oracle database](https://github.com/spring-attic/spring-data-jdbc-ext).

I just copy and modify some code that I think it is necessary for me.

- [Spring Jdbc Oracle](#spring-jdbc-oracle)
  - [Before You Start](#before-you-start)
  - [How to test](#how-to-test)
    - [I. Use testcontainer](#i-use-testcontainer)
    - [II. Use `docker-compose.yml`](#ii-use-docker-composeyml)

## Before You Start

I tested on below Software/Framework version and I am **NOT** sure it can work on another platform.

| **Software/Framework** | **Version**                            |
|------------------------|----------------------------------------|
| Java                   | 17                                     |
| Maven                  | 3.9.8                                  |
| Docker                 | 27.0.3                                 |
| Oracle Image           | gvenzl/oracle-xe:21.3.0-slim-faststart |
| Spring Framework       | 6.1.10                                 |
| Spring Boot            | 3.3.1                                  |

## How to test

### I. Use testcontainer

1. Open cmd and type

     ```cmd
     spring-jdbc-oracle> docker compose up
     ```

2. After docker runs finish, you just run `ExampleServiceContainerTest.java`

### II. Use `docker-compose.yml`

1. You just run `ExampleServiceTest.java`