package io.spring.jdbc.oracle.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.jdbc.oracle.dto.ComplexCustomerPojo;
import io.spring.jdbc.oracle.dto.ComplexCustomerRecord;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
class ComplexExampleRepositoryTest {

    @Autowired
    ComplexExampleRepository complexExampleRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("classpath:script/complex_example_pack.sql")
    Resource pack;

    @BeforeEach
    void init() throws IOException {

        var statement = pack.getContentAsString(StandardCharsets.UTF_8)
            .split("/");
        Arrays.stream(statement)
            .forEach(this.jdbcTemplate::update);
    }

    @Test
    void callExamplePack() {

        // arrange
        var address1 = new ComplexCustomerPojo.Address(
            "district 1",
            "city 1",
            List.of(1, 2)
        );
        var address2 = new ComplexCustomerPojo.Address(
            "district 2",
            "city 2",
            List.of(3, 4)
        );
        var pojo = new ComplexCustomerPojo(
            LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
            address1,
            new ComplexCustomerPojo.Address[]{address1, address2}
        );

        var address3 = new ComplexCustomerRecord.Address(
            "district 3",
            "city 3",
            new String[]{"1", "2"}
        );
        var address4 = new ComplexCustomerRecord.Address(
            "district 4",
            "city 4",
            new String[]{"3", "4"}
        );
        var recordObject = new ComplexCustomerRecord(
            Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)),
            address3,
            List.of(address3, address4)
        );

        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("OUT_CUSTOMER", pojo);
        expectOutput.put("OUT_CUSTOMERS", new ComplexCustomerRecord[]{recordObject});

        // assert
        assertThat(complexExampleRepository.callExamplePack())
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

}