# Convert to Java

<!-- TOC -->
* [Convert to Java](#convert-to-java)
  * [Parameter Input](#parameter-input)
  * [Parameter Output](#parameter-output)
<!-- TOC -->

## Parameter Input

**`ParameterInput.java` supports Type Input or Type Input Output**

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
        .withValues(...) // for multiple values
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

## Parameter Output

**`ParameterOutput.java` supports Type Output**

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
    -- in/inout parameters
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
        .withStructArray(...) // schema.package.array_type_name
        .withStruct(...) // schema.package.type_name

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