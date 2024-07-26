package io.spring.jdbc.oracle.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.jdbc.oracle.TestConfig;
import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.converter.support.DefaultOracleConverters;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@Import(TestConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DelegateMapperTest {

    private static final String PERSON_TYPE = "PERSON";

    @Autowired
    DataSource dataSource;

    Connection connection;
    JdbcTemplate jdbcTemplate;

    @BeforeAll
    void init() throws SQLException {

        connection = dataSource.getConnection();
        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update("""
            CREATE OR REPLACE TYPE person AS OBJECT (
                    first_name VARCHAR(255),
                    last_name  VARCHAR(255),
                    age        NUMBER,
                    birthdate  DATE
            );
            """);
    }

    @Nested
    class PojoClass {

        @Test
        void createStruct() throws SQLException {

            // arrange
            var mapper = DelegateMapper.newInstance(Person.class);

            // assert
            var expectOutput = connection.createStruct(
                PERSON_TYPE,
                new Object[]{
                    "hello@world",
                    "info",
                    BigDecimal.TEN,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                }
            );

            var output = mapper.toStruct(
                connection,
                PERSON_TYPE,
                new Person(
                    "hello@world",
                    "info",
                    BigDecimal.TEN,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                )
            );
            assertThat(output)
                .isEqualTo(expectOutput);

            // always return null if convert process is fail
            output = mapper.toStruct(
                connection,
                "EXAMPLE_PACK.CUSTOMER",
                null
            );
            assertThat(output)
                .isNull();
        }

        @Test
        void fromStruct() throws SQLException {

            // arrange
            var mapper = DelegateMapper.newInstance(Person.class);
            var struct = connection.createStruct(
                PERSON_TYPE,
                new Object[]{
                    "hello@world",
                    "info",
                    BigDecimal.TEN,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                }
            );

            // assert
            var output = mapper.fromStruct(connection, struct);
            assertThat(output)
                .usingRecursiveComparison()
                .isEqualTo(
                    new Person(
                        "hello@world",
                        "info",
                        BigDecimal.TEN,
                        Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                    )
                );

            // always return null if convert process is fail
            output = mapper.fromStruct(connection, null);
            assertThat(output)
                .isNull();
        }

        @Test
        void convert() {

            // arrange
            var mapper = DelegateMapper.newInstance(Person.class);

            // assert
            var output = mapper.convert(
                Map.of(
                    "first_name", "hello@world",
                    "lastname", "info",
                    "AGE", BigDecimal.TEN,
                    "biRthDate",
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                )
            );
            assertThat(output)
                .usingRecursiveComparison()
                .isEqualTo(
                    new Person(
                        "hello@world",
                        "info",
                        BigDecimal.TEN,
                        Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                    )
                );

            // always return null if convert process is fail
            output = mapper.fromStruct(connection, null);
            assertThat(output)
                .isNull();
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Person {

            private String firstName;
            private String lastName;
            private BigDecimal age;
            private Timestamp birthDate;
        }

    }

    @Nested
    class RecordClass {

        @Test
        void createStruct() throws SQLException {

            // arrange
            var mapper = DelegateMapper.newInstance(Person.class);

            // assert
            var expectOutput = connection.createStruct(
                PERSON_TYPE,
                new Object[]{
                    "hello@world",
                    "info",
                    BigDecimal.TEN,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                }
            );

            var output = mapper.toStruct(
                connection,
                PERSON_TYPE,
                new Person(
                    "hello@world",
                    "info",
                    BigDecimal.TEN,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                )
            );
            assertThat(output)
                .isEqualTo(expectOutput);

            // always return null if convert process is fail
            output = mapper.toStruct(
                connection,
                "EXAMPLE_PACK.CUSTOMER",
                null
            );
            assertThat(output)
                .isNull();
        }

        @Test
        void fromStruct() throws SQLException {

            // arrange
            var mapper = DelegateMapper.newInstance(Person.class);
            var struct = connection.createStruct(
                PERSON_TYPE,
                new Object[]{
                    "hello@world",
                    "info",
                    BigDecimal.TEN,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                }
            );

            // assert
            var output = mapper.fromStruct(connection, struct);
            assertThat(output)
                .usingRecursiveComparison()
                .isEqualTo(
                    new Person(
                        "hello@world",
                        "info",
                        BigDecimal.TEN,
                        Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                    )
                );

            // always return null if convert process is fail
            output = mapper.fromStruct(connection, null);
            assertThat(output)
                .isNull();
        }

        @Test
        void convert() {

            // arrange
            var mapper = DelegateMapper.newInstance(Person.class);

            // assert
            var output = mapper.convert(
                Map.of(
                    "first_name", "hello@world",
                    "lastname", "info",
                    "AGE", BigDecimal.TEN,
                    "biRthDate",
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                )
            );
            assertThat(output)
                .usingRecursiveComparison()
                .isEqualTo(
                    new Person(
                        "hello@world",
                        "info",
                        BigDecimal.TEN,
                        Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                    )
                );

            // always return null if convert process is fail
            output = mapper.fromStruct(connection, null);
            assertThat(output)
                .isNull();
        }

        record Person(
            String firstName,
            String lastName,
            BigDecimal age,
            Timestamp birthDate) {

        }

    }

    @Test
    void setConverters() {

        // arrange
        var converters = DefaultOracleConverters.INSTANCE;
        converters.addConverter(new LocalDateTimeToLocalDate());

        var mapper = DelegateMapper.newInstance(Person.class);
        mapper.setConverters(converters);

        // assert
        var output = mapper.convert(
            Map.of(
                "first_name", "hello@world",
                "lastname", "info",
                "AGE", BigDecimal.TEN,
                "biRthDate", LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
            )
        );
        assertThat(output)
            .usingRecursiveComparison()
            .isEqualTo(
                new Person(
                    "hello@world",
                    "info",
                    BigDecimal.TEN.toPlainString(),
                    LocalDate.now()
                )
            );
    }

    public record LocalDateTimeToLocalDate() implements OracleConverter<LocalDateTime, LocalDate> {

        @Override
        public LocalDate convert(LocalDateTime source) {

            return Optional.of(source)
                .map(LocalDateTime::toLocalDate)
                .orElse(null);
        }

    }

    record Person(
        String firstName,
        String lastName,
        String age,
        LocalDate birthDate) {

    }

}