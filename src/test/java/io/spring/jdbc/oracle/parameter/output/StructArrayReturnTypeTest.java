package io.spring.jdbc.oracle.parameter.output;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.zaxxer.hikari.HikariDataSource;
import io.spring.jdbc.oracle.Customer;
import io.spring.jdbc.oracle.SetupTestData;
import io.spring.jdbc.oracle.config.ExampleConfig;
import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.DelegateMapper;
import java.math.BigDecimal;
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
class StructArrayReturnTypeTest extends SetupTestData {

    @Autowired
    HikariDataSource dataSource;

    @Test
    void convert_array_not_contains_struct() throws SQLException {

        var mapper = DelegateMapper.newInstance(Customer.class).get();
        var returnType = new StructArrayReturnType<>(mapper);
        try (var connection = dataSource.getConnection().unwrap(OracleConnection.class)) {

            var numbers = connection.createOracleArray(
                "EXAMPLE_PACK.NUMBERS",
                new Object[]{BigDecimal.TEN, BigDecimal.ONE}
            );

            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> returnType.convertArray(connection, numbers));

            var nullValues = connection.createOracleArray(
                "EXAMPLE_PACK.NUMBERS",
                new Object[]{null}
            );

            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> returnType.convertArray(connection, nullValues));
        }

    }

}