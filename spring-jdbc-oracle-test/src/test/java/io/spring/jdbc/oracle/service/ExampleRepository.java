package io.spring.jdbc.oracle.service;

import io.spring.jdbc.oracle.dto.CustomerPojo;
import io.spring.jdbc.oracle.dto.CustomerRecord;
import io.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.spring.jdbc.oracle.parameter.output.ParameterOutput;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ExampleRepository {

    private static final String OBJECT = "customer_object";
    private static final String NUMBERS = "example_pack.numbers";
    private static final String CUSTOMERS = "example_pack.customers";
    private static final String CUSTOMER = "example_pack.customer";

    private final JdbcTemplate jdbcTemplate;

    Map<String, Object> callExamplePack() {

        this.jdbcTemplate.setResultsMapCaseInsensitive(true);

        // in
        var inCustomerObject = ParameterInput.withParameterName("in_customer_object")
            .withValue(null)
            .withStruct(OBJECT);
        var inNumbers = ParameterInput.withParameterName("in_numbers")
            .withValues(BigDecimal.ONE, BigDecimal.ZERO)
            .withArray(NUMBERS);
        var inCustomer = ParameterInput.withParameterName("in_customer", CustomerPojo.class)
            .withValue(new CustomerPojo("Nhan", "Tran", BigDecimal.ONE))
            .withStruct(CUSTOMER);
        var inCustomers = ParameterInput.withParameterName("in_customers", CustomerRecord.class)
            .withValues(
                List.of(
                    new CustomerRecord("Tran", "Nhan", BigDecimal.ZERO),
                    new CustomerRecord("Nhan", "Tran", BigDecimal.TEN)
                )
            )
            .withStructArray(CUSTOMERS, CUSTOMER);

        // inout
        var inOutCustomerObject = ParameterInput.withParameterName("in_out_customer_object")
            .withValue(null)
            .withStruct(OBJECT);
        var inOutNumbers = ParameterInput.withParameterName("in_out_numbers")
            .withValues(List.of(BigDecimal.TEN, BigDecimal.ZERO))
            .withArray(NUMBERS);
        var inOutCustomer = ParameterInput.withParameterName(
                "in_out_customer",
                CustomerRecord.class
            )
            .withValue(new CustomerRecord("Tran", "Nhan", BigDecimal.TEN))
            .withStruct(CUSTOMER);
        var inOutCustomers = ParameterInput.withParameterName(
                "in_out_customers",
                CustomerPojo.class
            )
            .withValues(
                List.of(
                    new CustomerPojo("Nhan", "Tran", BigDecimal.ZERO),
                    new CustomerPojo("Tran", "Nhan", BigDecimal.ONE)
                )
            )
            .withStructArray(CUSTOMERS, CUSTOMER);

        // out
        var outCustomerObject = ParameterOutput.withParameterName("out_customer_object")
            .withStruct(OBJECT);
        var outNumbers = ParameterOutput.withParameterName("out_numbers")
            .withArray(NUMBERS);
        var outCustomer = ParameterOutput.withParameterName("out_customer", CustomerPojo.class)
            .withStruct(CUSTOMER);
        var outCustomers = ParameterOutput.withParameterName("out_customers", CustomerRecord.class)
            .withStructArray(CUSTOMERS);

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

        return simpleJdbcCall.execute(sqlParameterSource);
    }

}