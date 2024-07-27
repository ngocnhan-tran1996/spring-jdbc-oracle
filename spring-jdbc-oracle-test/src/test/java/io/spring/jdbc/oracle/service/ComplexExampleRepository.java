package io.spring.jdbc.oracle.service;

import io.spring.jdbc.oracle.dto.ComplexCustomerPojo;
import io.spring.jdbc.oracle.dto.ComplexCustomerRecord;
import io.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.spring.jdbc.oracle.parameter.output.ParameterOutput;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ComplexExampleRepository {

    private static final String CUSTOMERS = "complex_example_pack.customer_array";
    private static final String CUSTOMER = "complex_example_pack.customer";

    private final JdbcTemplate jdbcTemplate;

    Map<String, Object> callExamplePack() {

        this.jdbcTemplate.setResultsMapCaseInsensitive(true);

        var address1 = new ComplexCustomerRecord.Address(
            "district 1",
            "city 1",
            new String[]{"1", "2"}
        );
        var address2 = new ComplexCustomerRecord.Address(
            "district 2",
            "city 2",
            new String[]{"3", "4"}
        );
        var recordObject = new ComplexCustomerRecord(
            Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)),
            address1,
            List.of(address1, address2)
        );

        var address3 = new ComplexCustomerPojo.Address(
            "district 3",
            "city 3",
            List.of(1, 2)
        );
        var address4 = new ComplexCustomerPojo.Address(
            "district 4",
            "city 4",
            List.of(3, 4)
        );
        var pojo = new ComplexCustomerPojo(
            LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
            address3,
            new ComplexCustomerPojo.Address[]{address3, address4}
        );

        // in
        var inCustomer = ParameterInput.withParameterName(
                "in_customer",
                ComplexCustomerRecord.class
            )
            .withValue(recordObject)
            .withStruct(CUSTOMER);
        var inCustomers = ParameterInput.withParameterName(
                "in_customers",
                ComplexCustomerPojo.class
            )
            .withValues(pojo)
            .withStructArray(CUSTOMERS, CUSTOMER);

        // out
        var outCustomer = ParameterOutput.withParameterName(
                "out_customer",
                ComplexCustomerPojo.class
            )
            .withStruct(CUSTOMER);
        var outCustomers = ParameterOutput.withParameterName(
                "out_customers",
                ComplexCustomerRecord.class
            )
            .withStructArray(CUSTOMERS);

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