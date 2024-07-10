package io.ngocnhan_tran1996.spring.jdbc.oracle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
abstract class DatabaseTestContainer {

    static final DockerImageName IMAGE_NAME = DockerImageName
        .parse("gvenzl/oracle-xe:21.3.0-slim-faststart")
        .asCompatibleSubstituteFor("gvenzl/oracle-free");

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer(IMAGE_NAME);

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Value("classpath:script/example_pack.sql")
    Resource resource;

    @DynamicPropertySource
    static void oracleProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.datasource.hikari.schema", oracle::getUsername);
    }

    @BeforeEach
    void init() throws IOException {

        var description = resource.getContentAsString(StandardCharsets.UTF_8);
        var sql = description.split("/");

        for (var statement : sql) {

            jdbcTemplate.update(statement);
        }
    }

}