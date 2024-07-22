package io.spring.jdbc.oracle.parameter.input;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import io.spring.jdbc.oracle.Customer;
import io.spring.jdbc.oracle.SetupTestData;
import io.spring.jdbc.oracle.config.ExampleConfig;
import io.spring.jdbc.oracle.mapper.DelegateMapper;
import java.sql.SQLException;
import oracle.jdbc.OracleConnection;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(ExampleConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StructArrayTypeValueTest extends SetupTestData {

    @Autowired
    HikariDataSource dataSource;

    @Test
    void create_type_value_with_values_is_null() throws SQLException {

        var connection = dataSource.getConnection();
        var output = new StructArrayTypeValue<>(
            "EXAMPLE_PACK.CUSTOMERS",
            null,
            "EXAMPLE_PACK.CUSTOMERS",
            DelegateMapper.newInstance(Customer.class).get()
        )
            .createTypeValue(connection, "EXAMPLE_PACK.CUSTOMERS");

        var expectOutput = connection
            .unwrap(OracleConnection.class)
            .createOracleArray("EXAMPLE_PACK.CUSTOMERS", null);

        assertThat(output)
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

}