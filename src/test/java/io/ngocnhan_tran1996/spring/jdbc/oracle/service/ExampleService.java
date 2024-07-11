package io.ngocnhan_tran1996.spring.jdbc.oracle.service;

import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output.ParameterOutput;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
class ExampleService {

    private final JdbcTemplate jdbcTemplate;

    public ExampleService(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    Map<String, Object> callExamplePack() {

        this.jdbcTemplate.setResultsMapCaseInsensitive(true);

        var inNumbers = ParameterInput.withParameterName("in_numbers")
            .withValues(BigDecimal.ONE, BigDecimal.TWO)
            .withArray("example_pack.numbers");
        var inCustomer = ParameterInput.withParameterName("in_customer", Customer.class)
            .withValue(new Customer("Nhan", "Tran", BigDecimal.ONE))
            .withStruct("example_pack.customer");
        var inCustomers = ParameterInput.withParameterName("in_customers", Customer.class)
            .withValues(
                List.of(
                    new Customer("Tran", "Nhan", BigDecimal.TWO),
                    new Customer("Nhan", "Tran", BigDecimal.TEN)
                )
            )
            .withStructArray("example_pack.customers", "example_pack.customer");

        var inOutNumbers = ParameterInput.withParameterName("in_out_numbers")
            .withValues(List.of(BigDecimal.TEN, BigDecimal.ZERO))
            .withArray("example_pack.numbers");
        var inOutCustomer = ParameterInput.withParameterName("in_out_customer", Customer.class)
            .withValue(new Customer("Tran", "Nhan", BigDecimal.TEN))
            .withStruct("example_pack.customer");
        var inOutCustomers = ParameterInput.withParameterName("in_out_customers", Customer.class)
            .withValues(
                List.of(
                    new Customer("Nhan", "Tran", BigDecimal.ZERO),
                    new Customer("Tran", "Nhan", BigDecimal.ONE)
                )
            )
            .withStructArray("example_pack.customers", "example_pack.customer");

        var outNumbers = ParameterOutput.withParameterName("out_numbers")
            .withArray("example_pack.numbers");
        var outCustomer = ParameterOutput.withParameterName("out_customer", Customer.class)
            .withStruct("example_pack.customer");
        var outCustomers = ParameterOutput.withParameterName("out_customers", Customer.class)
            .withStructArray("example_pack.customers");

        var simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate)
            .withCatalogName("example_pack")
            .withProcedureName("example_proc")
            .declareParameters(

                inNumbers.sqlParameter(),
                inCustomer.sqlParameter(),
                inCustomers.sqlParameter(),

                inOutNumbers.sqlInOutParameter(),
                inOutCustomer.sqlInOutParameter(),
                inOutCustomers.sqlInOutParameter(),

                outNumbers.sqlOutParameter(),
                outCustomer.sqlOutParameter(),
                outCustomers.sqlOutParameter()
            );

        var sqlParameterSource = new MapSqlParameterSource()
            .addValues(inNumbers.toMap())
            .addValues(inCustomer.toMap())
            .addValues(inCustomers.toMap())

            .addValues(inOutNumbers.toMap())
            .addValues(inOutCustomer.toMap())
            .addValues(inOutCustomers.toMap());

        return simpleJdbcCall.execute(sqlParameterSource);
    }

}