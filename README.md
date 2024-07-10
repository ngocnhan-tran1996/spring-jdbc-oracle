# Spring Jdbc Oracle

This repo references from [Spring Data JDBC Extensions for the Oracle database](https://github.com/spring-attic/spring-data-jdbc-ext).
I just copy and modify some code that I think it is necessary for me.

## Before You Start

I tested on below Software/Framework version and I am **NOT** sure it can work on another platform.

| **Software**     | **Version** |
|------------------|-------------|
| Java             | 17          |
| Maven            | 3.9.8       |


| **Framework**    | **Version**  |
|------------------|--------------|
| Spring Framework | 6.1.10       |
| Spring Boot      | 3.3.1        |


## How to test

### Use `docker-compose.yml`

1. Open cmd and type

    ```cmd
    spring-jdbc-oracle> docker compose up
    ```
2. After docker runs finish, you just run `JdbcServiceContainerTest.java`

### Use 

1. Wait until docker run finish and run `JdbcServiceContainerTest.java`