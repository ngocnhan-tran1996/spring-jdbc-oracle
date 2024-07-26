package io.spring.jdbc.oracle.converter.support;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.jdbc.oracle.converter.OracleConverters;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DefaultOracleConvertersTest {

    static final OracleConverters oracleConverters = DefaultOracleConverters.INSTANCE;

    void return_null_if_input_is_null(TypeDescriptor sourceType, TypeDescriptor targetType) {

        assertThat(oracleConverters.convert(null, sourceType, targetType))
            .isNull();
    }

    @Nested
    class DetermineJavaClassForJdbcTypeCode {

        @Test
        void return_Timestamp_from_LocalDateTime() {

            assertThat(oracleConverters.determineJavaClassForJdbcTypeCode(LocalDateTime.class))
                .isEqualTo(Timestamp.class);

            assertThat(oracleConverters.determineJavaClassForJdbcTypeCode(null))
                .isNull();
        }

    }

    @Nested
    class GenericConverter {

    }

    @Nested
    class Converter {

        @Test
        void convert_LocalDateTime_to_Timestamp() {

            var source = TypeDescriptor.valueOf(LocalDateTime.class);
            var target = TypeDescriptor.valueOf(Timestamp.class);

            var output = oracleConverters.convert(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                source,
                target
            );
            assertThat(output)
                .isEqualTo(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)));

            return_null_if_input_is_null(source, target);
        }

        @Test
        void convert_Timestamp_to_LocalDateTime() {

            var source = TypeDescriptor.valueOf(Timestamp.class);
            var target = TypeDescriptor.valueOf(LocalDateTime.class);

            var output = oracleConverters.convert(
                Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)),
                source,
                target
            );
            assertThat(output)
                .isEqualTo(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));

            return_null_if_input_is_null(source, target);
        }

    }

}