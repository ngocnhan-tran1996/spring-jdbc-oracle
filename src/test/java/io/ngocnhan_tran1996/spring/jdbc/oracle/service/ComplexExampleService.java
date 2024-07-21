package io.ngocnhan_tran1996.spring.jdbc.oracle.service;

import io.ngocnhan_tran1996.spring.jdbc.oracle.accessor.ClassRecord;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output.ParameterOutput;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
class ComplexExampleService {

    private final JdbcTemplate jdbcTemplate;

    public ComplexExampleService(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    <T, C> Map<String, Object> callComplexExamplePack(Class<T> mappedClass, Class<C> childClass)
        throws NoSuchMethodException {

        this.jdbcTemplate.setResultsMapCaseInsensitive(true);

        var isRecord = new ClassRecord<>(mappedClass).isTypeRecord();
        var addressClass = childClass.getDeclaredConstructor(
            String.class,
            String.class,
            List.class
        );

        var array = Array.newInstance(childClass, 1);
        var customerClass = mappedClass.getDeclaredConstructor(
            String.class,
            String.class,
            BigDecimal.class,
            Timestamp.class,
            childClass,
            isRecord
                ? List.class
                : array.getClass()
        );

        var address = BeanUtils.instantiateClass(
            addressClass,
            "district",
            "city",
            List.of("1", "2")
        );

        Array.set(array, 0, address);
        var customer = BeanUtils.instantiateClass(
            customerClass,
            "Nhan",
            "Tran",
            BigDecimal.TEN,
            Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)),
            address,
            isRecord
                ? List.of(address)
                : array
        );

        var inCustomer = ParameterInput.withParameterName("in_customer", mappedClass)
            .withValue(customer)
            .withStruct("complex_example_pack.customer");
        var inCustomers = ParameterInput.withParameterName("in_customers", mappedClass)
            .withValues(customer)
            .withStructArray("complex_example_pack.customers", "complex_example_pack.customer");

        var outCustomer = ParameterOutput.withParameterName("out_customer", mappedClass)
            .withStruct("complex_example_pack.customer");
        var outCustomers = ParameterOutput.withParameterName("out_customers", mappedClass)
            .withStructArray("complex_example_pack.customers");

        var simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate)
            .withCatalogName("complex_example_pack")
            .withProcedureName("EXAMPLE_PROC")
            .declareParameters(
                inCustomer.sqlParameter(),
                inCustomers.sqlParameter(),

                outCustomer.sqlOutParameter(),
                outCustomers.sqlOutParameter()
            );

        var sqlParameterSource = new MapSqlParameterSource()
            .addValues(inCustomer.toMap())
            .addValues(inCustomers.toMap());

        return simpleJdbcCall.execute(sqlParameterSource);
    }

}