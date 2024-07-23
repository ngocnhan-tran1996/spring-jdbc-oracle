title: Convention
author: Tran Ngoc Nhan

# Convention

<!-- TOC -->
* [Convention](#convention)
  * [Data Convention](#data-convention)
  * [Naming Convention](#naming-convention)
  * [Annotation OracleParameter](#annotation-oracleparameter)
<!-- TOC -->

## Data Convention

| **Type Data** | **Java Data** |
|---------------|---------------|
| NUMBER        | BigDecimal    |
| VARCHAR       | String        |
| DATE          | Timestamp     |


## Naming Convention

I have a small comparison.

| **Type Name** | **Field Name** |
|---------------|----------------|
| last_name     | last_name      |
| last_name     | lastName       |
| LAST_NAME     | lastName       |
| LASTNAME      | lastName       |


## Annotation OracleParameter

Use annotation `OracleParameter` in case
- Both field names are not the same
- Type input/output is a complex type

Please take a look [ComplexCustomer.java](https://github.com/ngocnhan-tran1996/spring-jdbc-oracle/blob/main/src/test/java/io/spring/jdbc/oracle/ComplexCustomer.java) and [`complex_example_pack.sql`](https://github.com/ngocnhan-tran1996/spring-jdbc-oracle/tree/main/src/test/resources/script/complex_example_pack.sql)

You will see how to use annotation `OracleParameter`

```java
@OracleParameter(
    value = "original_address",
    input = @OracleType(structName = "complex_example_pack.address"),
    output = @OracleType(structName = "complex_example_pack.address")
)
private Address address;

@OracleParameter(
    value = "other_addresses",
    input = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.addresses"),
    output = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.addresses")
)
private Address[] addresses;
```