package io.ngocnhan_tran1996.spring.jdbc.oracle;

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
public class JdbcService {

    private final JdbcTemplate jdbcTemplate;

    public JdbcService(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    Map<String, Object> callExamplePack() {

        this.jdbcTemplate.setResultsMapCaseInsensitive(true);

        var inNumbers = ParameterInput.withParameterName("in_numbers")
            .withValues(BigDecimal.ONE, BigDecimal.TWO)
            .withArray("sys.example_pack.numbers");
        var inCustomer = ParameterInput.withParameterName("in_customer", Customer.class)
            .withValue(new Customer("Nhan", "Tran", BigDecimal.ONE))
            .withStruct("sys.example_pack.customer");
        var inCustomers = ParameterInput.withParameterName("in_customers", Customer.class)
            .withValues(
                List.of(
                    new Customer("Tran", "Nhan", BigDecimal.TWO),
                    new Customer("Nhan", "Tran", BigDecimal.TEN)
                )
            )
            .withStructArray("sys.example_pack.customers", "sys.example_pack.customer");

        var inOutNumbers = ParameterInput.withParameterName("in_out_numbers")
            .withValues(List.of(BigDecimal.TEN, BigDecimal.ZERO))
            .withArray("sys.example_pack.numbers");
        var inOutCustomer = ParameterInput.withParameterName("in_out_customer", Customer.class)
            .withValue(new Customer("Tran", "Nhan", BigDecimal.TEN))
            .withStruct("sys.example_pack.customer");
        var inOutCustomers = ParameterInput.withParameterName("in_out_customers", Customer.class)
            .withValues(
                List.of(
                    new Customer("Nhan", "Tran", BigDecimal.ZERO),
                    new Customer("Tran", "Nhan", BigDecimal.ONE)
                )
            )
            .withStructArray("sys.example_pack.customers", "sys.example_pack.customer");

        var outNumbers = ParameterOutput.withParameterName("out_numbers")
            .withArray("sys.example_pack.numbers");
        var outCustomer = ParameterOutput.withParameterName("out_customer", Customer.class)
            .withStruct("sys.example_pack.customer");
        var outCustomers = ParameterOutput.withParameterName("out_customers", Customer.class)
            .withStructArray("sys.example_pack.customers");

        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withSchemaName("SYS")
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