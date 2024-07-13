package io.ngocnhan_tran1996.spring.jdbc.oracle.service;

import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output.ParameterOutput;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
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

    <T> Map<String, Object> callExamplePack(Class<T> mappedClass) throws NoSuchMethodException {

        this.jdbcTemplate.setResultsMapCaseInsensitive(true);

        var customer = mappedClass.getDeclaredConstructor(String.class, String.class,
            BigDecimal.class);

        var inCustomerObject = ParameterInput.withParameterName("in_customer_object")
            .withValue(null)
            .withStruct("customer_object");
        var inNumbers = ParameterInput.withParameterName("in_numbers")
            .withValues(BigDecimal.ONE, BigDecimal.ZERO)
            .withArray("example_pack.numbers");
        var inCustomer = ParameterInput.withParameterName("in_customer", mappedClass)
            .withValue(BeanUtils.instantiateClass(customer, "Nhan", "Tran", BigDecimal.ONE))
            .withStruct("example_pack.customer");
        var inCustomers = ParameterInput.withParameterName("in_customers", mappedClass)
            .withValues(
                List.of(
                    BeanUtils.instantiateClass(customer, "Tran", "Nhan", BigDecimal.ZERO),
                    BeanUtils.instantiateClass(customer, "Nhan", "Tran", BigDecimal.TEN)
                )
            )
            .withStructArray("example_pack.customers", "example_pack.customer");

        var inOutCustomerObject = ParameterInput.withParameterName("in_out_customer_object")
            .withValue(null)
            .withStruct("customer_object");
        var inOutNumbers = ParameterInput.withParameterName("in_out_numbers")
            .withValues(List.of(BigDecimal.TEN, BigDecimal.ZERO))
            .withArray("example_pack.numbers");
        var inOutCustomer = ParameterInput.withParameterName("in_out_customer", mappedClass)
            .withValue(BeanUtils.instantiateClass(customer, "Tran", "Nhan", BigDecimal.TEN))
            .withStruct("example_pack.customer");
        var inOutCustomers = ParameterInput.withParameterName("in_out_customers", mappedClass)
            .withValues(
                List.of(
                    BeanUtils.instantiateClass(customer, "Nhan", "Tran", BigDecimal.ZERO),
                    BeanUtils.instantiateClass(customer, "Tran", "Nhan", BigDecimal.ONE)
                )
            )
            .withStructArray("example_pack.customers", "example_pack.customer");

        var outCustomerObject = ParameterOutput.withParameterName("out_customer_object")
            .withStruct("customer_object");
        var outNumbers = ParameterOutput.withParameterName("out_numbers")
            .withArray("example_pack.numbers");
        var outCustomer = ParameterOutput.withParameterName("out_customer", mappedClass)
            .withStruct("example_pack.customer");
        var outCustomers = ParameterOutput.withParameterName("out_customers", mappedClass)
            .withStructArray("example_pack.customers");

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