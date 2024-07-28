package io.spring.jdbc.oracle.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.spring.jdbc.oracle.TestConfig;
import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.annotation.OracleType;
import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.converter.support.DefaultOracleConverters;
import io.spring.jdbc.oracle.exception.ValueException;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oracle.jdbc.driver.OracleConnection;
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

    private static final String PERSON_TYPE = "PERSON_OBJ";

    @Autowired
    DataSource dataSource;

    Connection connection;
    JdbcTemplate jdbcTemplate;

    @BeforeAll
    void init() throws SQLException {

        connection = dataSource.getConnection();
        jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update("""
            CREATE OR REPLACE TYPE person_obj AS OBJECT (
                    first_name VARCHAR(255),
                    last_name  VARCHAR(255),
                    age        NUMBER,
                    birthdate  DATE
            );
            """);
    }

    @Test
    void convert() {

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

        assertThat(mapper.convert(null))
            .isNull();
    }

    void fromStruct_return_null_if_convert_process_is_failed(DelegateMapper<?> mapper) {

        var output = mapper.fromStruct(connection, null);
        assertThat(output)
            .isNull();
    }

    void toStruct_return_null_if_convert_process_is_failed(DelegateMapper<?> mapper) {

        var output = mapper.toStruct(connection, PERSON_TYPE, null);
        assertThat(output)
            .isNull();
    }

    public record LocalDateTimeToLocalDate() implements OracleConverter<LocalDateTime, LocalDate> {

        @Override
        public LocalDate convert(LocalDateTime source) {

            return Optional.of(source)
                .map(LocalDateTime::toLocalDate)
                .orElse(null);
        }

    }

    public record StringToTString() implements OracleConverter<String, String> {

        @Override
        public String convert(String source) {

            return source;
        }

    }

    record Person(
        String firstName,
        String lastName,
        String age,
        LocalDate birthDate) {

    }

    @Nested
    class PojoClass {

        @Test
        void toStruct() throws SQLException {

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

            toStruct_return_null_if_convert_process_is_failed(mapper);
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

            fromStruct_return_null_if_convert_process_is_failed(mapper);
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
                    "AGE", BigDecimal.TEN
                )
            );
            assertThat(output)
                .usingRecursiveComparison()
                .isEqualTo(
                    new Person(
                        "hello@world",
                        "info",
                        BigDecimal.TEN,
                        null
                    )
                );
        }

        @Getter
        @Setter
        @NoArgsConstructor
        static class Person {

            @OracleParameter(
                input = @OracleType(converter = StringToTString.class),
                output = @OracleType(converter = StringToTString.class)
            )
            private String firstName;
            private String lastName;
            private BigDecimal age;
            private Timestamp birthDate;
            private String unknown;

            public Person(String firstName, String lastName, BigDecimal age, Timestamp birthDate) {

                this.firstName = firstName;
                this.lastName = lastName;
                this.age = age;
                this.birthDate = birthDate;
            }

        }

    }

    @Nested
    class RecordClass {

        @Test
        void toStruct() throws SQLException {

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

            toStruct_return_null_if_convert_process_is_failed(mapper);
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

            fromStruct_return_null_if_convert_process_is_failed(mapper);
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
        }

        record Person(
            String firstName,
            String lastName,
            BigDecimal age,
            Timestamp birthDate) {

        }

    }

    @Nested
    class MethodSpec {

        @Test
        void throw_exception_if_class_have_same_property_name() {

            // assert
            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> DelegateMapper.newInstance(PersonSetterPojo.class));

            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> DelegateMapper.newInstance(PersonGetterPojo.class));

            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> DelegateMapper.newInstance(PersonRecord.class));
        }

        @Test
        void throw_exception_if_record_class_has_no_field() {

            // assert
            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> DelegateMapper.newInstance(PersonNoFieldRecord.class));
        }

        @Test
        void mock_throw_exception_if_class_has_no_same_type() throws SQLException {

            // arrange
            var mapper = DelegateMapper.newInstance(PersonWrongBirthDatePojo.class);
            mapper.setConverters(null);

            // assert
            var output = mapper.toStruct(
                connection.unwrap(OracleConnection.class),
                PERSON_TYPE,
                new PersonWrongBirthDatePojo()
            );
            assertThat(output)
                .isNull();

        }

        @Setter
        static class PersonSetterPojo {

            String firstName;

            @OracleParameter("firstName")
            String lastName;

        }

        @Getter
        static class PersonGetterPojo {

            private final String firstName = "X";

            @OracleParameter("firstName")
            private final String lastName = "X";
        }

        @Getter
        static class PersonWrongBirthDatePojo {

            private final String birthDate = "TEST";
        }

        record PersonRecord(
            String firstName,
            @OracleParameter("firstName")
            String lastName) {

        }

        record PersonNoFieldRecord() {

        }

    }

}