package io.spring.jdbc.oracle.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.jdbc.oracle.TestConfig;
import java.io.IOException;
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
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Import(TestConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PersonExampleRepositoryTest {

    @Autowired
    PersonExampleRepository personExampleRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("classpath:script/person_example_pack.sql")
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
        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("OUT_PERSON", null);
        expectOutput.put("OUT_PERSONS", null);

        // assert
        assertThat(personExampleRepository.callExamplePack())
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

}