# Spring Jdbc Oracle

This repo references from [Spring Data JDBC Extensions for the Oracle database](https://github.com/spring-attic/spring-data-jdbc-ext).

I just copy and modify some code that I think it is necessary for me.

## Table of Contents

- [Spring Jdbc Oracle](#spring-jdbc-oracle)
  - [Table of Contents](#table-of-contents)
  - [Before You Start](#before-you-start)
  - [How To Test](#how-to-test)
  - [Usage](#usage)
    - [Type Input](#type-input)
    - [Type Input Output](#type-input-output)
    - [Type Output](#type-output)
  - [Reference](#reference)

## Before You Start

I tested on below Software/Framework version and I am **NOT** sure it can work on another platform.

| **Software/Framework** | **Version**                            |
|------------------------|----------------------------------------|
| Java                   | 17                                     |
| Maven                  | 3.9.8                                  |
| Docker                 | 27.0.3                                 |
| Oracle Image           | gvenzl/oracle-free:23.4-slim-faststart |
| Spring Framework       | 6.1.10                                 |
| Spring Boot            | 3.3.1                                  |

## How To Test

Just run `ExampleServiceTest.java`

## Usage

I am a developer and I am so lazy. If you struggle with Oracle types like me, this repository can help you.

**Example**

```oracle
TYPE customer IS RECORD (
        first_name VARCHAR(255),
        last_name  VARCHAR(255),
        age        NUMBER
);

TYPE customers IS
    TABLE OF customer;

TYPE numbers IS
    TABLE OF NUMBER INDEX BY BINARY_INTEGER;

PROCEDURE example_proc (
    in_numbers       IN numbers,
    in_customer      IN customer,
    in_customers     IN customers,
    in_out_numbers   IN OUT numbers,
    in_out_customer  IN OUT customer,
    in_out_customers IN OUT customers,
    out_numbers      OUT numbers,
    out_customer     OUT customer,
    out_customers    OUT customers
);
```

Please take a look and you will know how to use it

1. [Example script](src/test/resources/script/example_pack.sql)
2. [ExampleService](src/test/java/io/ngocnhan_tran1996/spring/jdbc/oracle/service/ExampleService.java) execute above script using `JdbcTemplate`

### Type Input

```java
var inNumbers = ParameterInput.withParameterName("in_numbers")
        .withValues(BigDecimal.ONE, BigDecimal.TWO)
        .withArray("example_pack.numbers");
```

### Type Input Output

```java
var inOutNumbers = ParameterInput.withParameterName("in_out_numbers")
        .withValues(List.of(BigDecimal.TEN, BigDecimal.ZERO))
        .withArray("example_pack.numbers");
```

### Type Output

```java
var outNumbers = ParameterOutput.withParameterName("out_numbers")
        .withArray("example_pack.numbers");
```

## Reference

- [Spring Data JDBC Extensions for the Oracle database](https://github.com/spring-attic/spring-data-jdbc-ext)
- [Handling Complex Types for Stored Procedure Calls](https://docs.spring.io/spring-framework/reference/data-access/jdbc/parameter-handling.html#jdbc-complex-types)