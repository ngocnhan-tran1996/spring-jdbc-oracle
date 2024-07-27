package io.spring.jdbc.oracle.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
//@Import(ExampleConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExampleRepositoryTest {

    @Autowired
    ExampleRepository exampleRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("classpath:script/example_pack.sql")
    Resource examplePack;

    @BeforeEach
    void init() throws IOException {

        var statement = examplePack.getContentAsString(StandardCharsets.UTF_8)
            .split("/");
        Arrays.stream(statement)
            .forEach(this.jdbcTemplate::update);
    }

    @Test
    void callExamplePack() {

        // arrange
        var outNumbers = new BigDecimal[]{BigDecimal.ONE, BigDecimal.ZERO};
        var outCustomer = new CustomerPojo("Nhan", "Tran", BigDecimal.ONE);
        var outCustomers = new CustomerRecord[]{
            new CustomerRecord("Tran", "Nhan", BigDecimal.ZERO),
            new CustomerRecord("Nhan", "Tran", BigDecimal.TEN)
        };

        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("IN_OUT_NUMBERS", outNumbers);
        expectOutput.put("OUT_NUMBERS", outNumbers);

        expectOutput.put("IN_OUT_CUSTOMER", new CustomerRecord("Nhan", "Tran", BigDecimal.ONE));
        expectOutput.put("OUT_CUSTOMER", outCustomer);

        expectOutput.put(
            "IN_OUT_CUSTOMERS",
            new CustomerPojo[]{
                new CustomerPojo("Tran", "Nhan", BigDecimal.ZERO),
                new CustomerPojo("Nhan", "Tran", BigDecimal.TEN)
            }
        );
        expectOutput.put("OUT_CUSTOMERS", outCustomers);

        expectOutput.put("IN_OUT_CUSTOMER_OBJECT", null);
        expectOutput.put("OUT_CUSTOMER_OBJECT", null);

        // assert
        assertThat(exampleRepository.callExamplePack())
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

}