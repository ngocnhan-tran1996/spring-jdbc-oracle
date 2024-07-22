package io.spring.jdbc.oracle.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.jdbc.oracle.ComplexCustomer;
import io.spring.jdbc.oracle.ComplexCustomerRecord;
import io.spring.jdbc.oracle.SetupTestData;
import io.spring.jdbc.oracle.config.ExampleConfig;
import io.spring.jdbc.oracle.ComplexCustomerRecord.Address;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(ExampleConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ComplexExampleServiceTest extends SetupTestData {

    @Autowired
    ComplexExampleService complexExampleService;

    @Test
    void with_type_Pojo() throws NoSuchMethodException {

        // arrange
        var address = new ComplexCustomer.Address();
        address.setCity("city");
        address.setDistrict("district");
        address.setValues(List.of("1", "2"));

        var customer = new ComplexCustomer(
            "Nhan",
            "Tran",
            BigDecimal.TEN,
            Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)),
            address,
            new ComplexCustomer.Address[]{address}
        );

        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("OUT_CUSTOMER", customer);
        expectOutput.put("OUT_CUSTOMERS", new ComplexCustomer[]{customer});

        // assert
        var output = complexExampleService.callComplexExamplePack(
            ComplexCustomer.class,
            ComplexCustomer.Address.class
        );
        assertThat(output)
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

    @Test
    void with_type_Record() throws NoSuchMethodException {

        // arrange
        var address = new Address(
            "district",
            "city",
            List.of("1", "2")
        );

        var customer = new ComplexCustomerRecord(
            "Nhan",
            "Tran",
            BigDecimal.TEN,
            Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)),
            address,
            List.of(address)
        );

        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("OUT_CUSTOMER", customer);
        expectOutput.put("OUT_CUSTOMERS", new ComplexCustomerRecord[]{customer});

        // assert
        var output = complexExampleService.callComplexExamplePack(
            ComplexCustomerRecord.class,
            ComplexCustomerRecord.Address.class
        );
        assertThat(output)
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

}