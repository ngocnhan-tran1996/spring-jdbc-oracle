# Spring Jdbc Oracle

This repo references from [Spring Data JDBC Extensions for the Oracle database](https://github.com/spring-attic/spring-data-jdbc-ext).

I just copy and modify some code that I think it is necessary for me.

## Table of Contents

- [Spring Jdbc Oracle](#spring-jdbc-oracle)
  - [Table of Contents](#table-of-contents)
  - [Before You Start](#before-you-start)
  - [How To Test](#how-to-test)
  - [Usage](#usage)
  - [Convert to Java](#convert-to-java)
    - [Type Input or Type Input Output](#type-input-or-type-input-output)
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

### Convert to Java

#### Type Input or Type Input Output

Use file: `ParameterInput.java`

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
    -- out parameters
);
```

```java
/**
 * if package is not blank, it will be `schema.package.type_name`, otherwise `schema.type_name`
 * /
ParameterInput.withParameterName(...) // parameter_name
        .withValues(...) // for multiple values and withValue for one value
        .withValues(List.of(...)) // for multiple values
        .withValue(...) // for one value
        .withArray(...) // schema.package.array_type_name
        .withStruct(...) // schema.package.type_name
        .withStructArray(...) // schema.package.array_type_name, schema.package.type_name

/**
 * use this in case type contains keyword "TYPE `numbers` IS TABLE OF `primitive type`"
 *
 * Note:
 * `numbers` is example, you can declare name whatever you want
 * `primitive type` is one of VARCHAR, NUMBER, NVARCHAR...
 * /
var inNumbers = ParameterInput.withParameterName("in_numbers")
    .withValues(...)
    .withArray("example_pack.numbers");

/**
 * use this in case type contains keyword either "TYPE `customer` IS RECORD" or "Type `customer` AS OBJECT"
 *
 * Note: `customer` is example, you can declare name whatever you want
 * /
var inCustomer = ParameterInput.withParameterName("in_customer", Customer.class)
    .withValue(...) // instance class Customer
    .withStruct("example_pack.customer");

/**
 * use this in case
 * 1. type contains keyword "TYPE `customers` IS TABLE OF `customer`"
 * 2. `customer` contains keyword either "TYPE customer IS RECORD" or "Type customer AS OBJECT"
 *
 * Note: `customers` and `customer` are example, you can declare name whatever you want
 * /
var inCustomers = ParameterInput.withParameterName("in_customers", Customer.class)
    .withValues(...) // instance multiple class Customer
    .withStructArray("example_pack.customers", "example_pack.customer");

public class Customer {

    @OracleParameter("first_name")
    private String name;
    private String lastName;
    private BigDecimal age;

    // getter/setter
}
```

#### Type Output

Use file: `ParameterOutput.java`

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
    -- in/inout parameters,
    out_numbers      OUT numbers,
    out_customer     OUT customer,
    out_customers    OUT customers
);
```

```java
/**
 * if package is not blank, it will be `schema.package.type_name`, otherwise `schema.type_name`
 * /
ParameterOutput.withParameterName(...) // parameter_name
        .withArray(...) // schema.package.array_type_name
        .withStruct(...) // schema.package.type_name
        .withStructArray(...) // schema.package.array_type_name, schema.package.type_name

/**
 * use this in case type contains keyword "TYPE `numbers` IS TABLE OF `primitive type`"
 *
 * Note:
 * `numbers` is example, you can declare name whatever you want
 * `primitive type` is one of VARCHAR, NUMBER, NVARCHAR...
 * /
var outNumbers = ParameterOutput.withParameterName("out_numbers")
    .withArray("example_pack.numbers");

/**
 * use this in case type contains keyword either "TYPE `customer` IS RECORD" or "Type `customer` AS OBJECT"
 *
 * Note: `customer` is example, you can declare name whatever you want
 * /
var outCustomer = ParameterOutput.withParameterName("out_customer", Customer.class)
    .withStruct("example_pack.customer");

/**
 * use this in case
 * 1. type contains keyword "TYPE `customers` IS TABLE OF `customer`"
 * 2. `customer` contains keyword either "TYPE customer IS RECORD" or "Type customer AS OBJECT"
 *
 * Note: `customers` and `customer` are example, you can declare name whatever you want
 * /
var outCustomers = ParameterOutput.withParameterName("out_customers", Customer.class)
    .withStructArray("example_pack.customers"); // no need type `customer`

public class Customer {

    @OracleParameter("first_name")
    private String name;
    private String lastName;
    private BigDecimal age;

    // getter/setter
}
```

### Implementation

`SimpleJdbcCall` and `MapSqlParameterSource` come from Spring.

```java
var simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate)
    .withCatalogName("example_pack")
    .withProcedureName("EXAMPLE_PROC")
    .declareParameters(

        inCustomerObject.sqlParameter(),
        inNumbers.sqlParameter(),
        inCustomer.sqlParameter(),
        inCustomers.sqlParameter(),

        inOutCustomerObject.sqlInOutParameter(),
        inOutNumbers.sqlInOutParameter(),
        inOutCustomer.sqlInOutParameter(),
        inOutCustomers.sqlInOutParameter(),

        outCustomerObject.sqlOutParameter(),
        outNumbers.sqlOutParameter(),
        outCustomer.sqlOutParameter(),
        outCustomers.sqlOutParameter()
    );

var sqlParameterSource = new MapSqlParameterSource()
    .addValues(inCustomerObject.toMap())
    .addValues(inNumbers.toMap())
    .addValues(inCustomer.toMap())
    .addValues(inCustomers.toMap())

    .addValues(inOutCustomerObject.toMap())
    .addValues(inOutNumbers.toMap())
    .addValues(inOutCustomer.toMap())
    .addValues(inOutCustomers.toMap());

// return Map<String, Object>
return simpleJdbcCall.execute(sqlParameterSource);

// the result, key is always uppercase
var result = new HashMap<String, Object>();
result.containsKey("IN_OUT_NUMBERS");
result.containsKey("OUT_NUMBERS");

result.containsKey("IN_OUT_CUSTOMER");
result.containsKey("OUT_CUSTOMER");

result.containsKey("IN_OUT_CUSTOMERS");
result.containsKey("OUT_CUSTOMERS");

result.containsKey("IN_OUT_CUSTOMER_OBJECT");
result.containsKey("OUT_CUSTOMER_OBJECT");
```

## Reference

- [Spring Data JDBC Extensions for the Oracle database](https://github.com/spring-attic/spring-data-jdbc-ext)
- [Handling Complex Types for Stored Procedure Calls](https://docs.spring.io/spring-framework/reference/data-access/jdbc/parameter-handling.html#jdbc-complex-types)