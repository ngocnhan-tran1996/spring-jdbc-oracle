package io.ngocnhan_tran1996.spring.jdbc.oracle;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
abstract class DatabaseTestContainer extends SetupTestData {

    static final DockerImageName IMAGE_NAME = DockerImageName
        .parse("gvenzl/oracle-xe:21.3.0-slim-faststart")
        .asCompatibleSubstituteFor("gvenzl/oracle-free");

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer(IMAGE_NAME);

    @DynamicPropertySource
    static void oracleProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.datasource.hikari.schema", oracle::getUsername);
    }

}