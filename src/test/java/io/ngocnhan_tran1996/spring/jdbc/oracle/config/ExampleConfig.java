package io.ngocnhan_tran1996.spring.jdbc.oracle.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.oracle.OracleContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ExampleConfig {

    @Bean
    @ServiceConnection
    OracleContainer oracleContainer(DynamicPropertyRegistry registry) {

        var oracle = new OracleContainer("gvenzl/oracle-free:23.4-slim-faststart");
        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.datasource.hikari.schema", oracle::getUsername);
        return oracle;
    }

}