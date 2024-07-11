package io.ngocnhan_tran1996.spring.jdbc.oracle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SetupTestData {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("classpath:script/example_pack.sql")
    Resource resource;

    @BeforeEach
    void init() throws IOException {

        var description = resource.getContentAsString(StandardCharsets.UTF_8);
        var sql = description.split("/");

        for (var statement : sql) {

            jdbcTemplate.update(statement);
        }
    }

}