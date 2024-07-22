title: Implementation
author: Tran Ngoc Nhan

# Implementation

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

// see the result and type
var result = simpleJdbcCall.execute(sqlParameterSource);
result.containsKey("IN_OUT_NUMBERS");
result.containsKey("OUT_NUMBERS"); // Array BigDecimal = BigDecimal[].class

result.containsKey("IN_OUT_CUSTOMER");
result.containsKey("OUT_CUSTOMER");

result.containsKey("IN_OUT_CUSTOMERS"); // Array Customer class = Customer[].class
result.containsKey("OUT_CUSTOMERS");

result.containsKey("IN_OUT_CUSTOMER_OBJECT");
result.containsKey("OUT_CUSTOMER_OBJECT");
```