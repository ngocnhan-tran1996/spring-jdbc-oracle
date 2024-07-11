package io.ngocnhan_tran1996.spring.jdbc.oracle.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.ngocnhan_tran1996.spring.jdbc.oracle.SetupTestData;
import io.ngocnhan_tran1996.spring.jdbc.oracle.config.ExampleConfig;
import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(ExampleConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleServiceTest extends SetupTestData {

    @Autowired
    ExampleService exampleService;

    @Test
    void with_type_Pojo() throws NoSuchMethodException {

        // arrange
        var outNumbers = new BigDecimal[]{BigDecimal.ONE, BigDecimal.ZERO};
        var outCustomer = new Customer("Nhan", "Tran", BigDecimal.ONE);
        var outCustomers = new Customer[]{
            new Customer("Tran", "Nhan", BigDecimal.ZERO),
            new Customer("Nhan", "Tran", BigDecimal.TEN)
        };

        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("IN_OUT_NUMBERS", outNumbers);
        expectOutput.put("OUT_NUMBERS", outNumbers);

        expectOutput.put("IN_OUT_CUSTOMER", outCustomer);
        expectOutput.put("OUT_CUSTOMER", outCustomer);

        expectOutput.put("IN_OUT_CUSTOMERS", outCustomers);
        expectOutput.put("OUT_CUSTOMERS", outCustomers);

        // assert
        assertThat(exampleService.callExamplePack(Customer.class))
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

    @Test
    void with_type_Record() throws NoSuchMethodException {

        // arrange
        var outNumbers = new BigDecimal[]{BigDecimal.ONE, BigDecimal.ZERO};
        var outCustomer = new CustomerRecord("Nhan", "Tran", BigDecimal.ONE);
        var outCustomers = new CustomerRecord[]{
            new CustomerRecord("Tran", "Nhan", BigDecimal.ZERO),
            new CustomerRecord("Nhan", "Tran", BigDecimal.TEN)
        };

        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("IN_OUT_NUMBERS", outNumbers);
        expectOutput.put("OUT_NUMBERS", outNumbers);

        expectOutput.put("IN_OUT_CUSTOMER", outCustomer);
        expectOutput.put("OUT_CUSTOMER", outCustomer);

        expectOutput.put("IN_OUT_CUSTOMERS", outCustomers);
        expectOutput.put("OUT_CUSTOMERS", outCustomers);

        // assert
        assertThat(exampleService.callExamplePack(CustomerRecord.class))
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

}