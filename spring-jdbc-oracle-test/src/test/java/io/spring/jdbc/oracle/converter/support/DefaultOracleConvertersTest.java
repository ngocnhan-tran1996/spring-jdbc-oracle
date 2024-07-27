package io.spring.jdbc.oracle.converter.support;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.jdbc.oracle.converter.OracleConverters;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

        @Test
        void convert_Number_to_String() {

            var source = TypeDescriptor.valueOf(Number.class);
            var target = TypeDescriptor.valueOf(String.class);

            var output = oracleConverters.convert((short) 1, source, target);
            assertThat(output).isEqualTo("1");

            output = oracleConverters.convert(1, source, target);
            assertThat(output).isEqualTo("1");

            output = oracleConverters.convert(1L, source, target);
            assertThat(output).isEqualTo("1");

            output = oracleConverters.convert(1F, source, target);
            assertThat(output).isEqualTo("1.0");

            output = oracleConverters.convert(BigDecimal.ONE, source, target);
            assertThat(output).isEqualTo("1");

            return_null_if_input_is_null(source, target);
        }

        @Test
        void convert_Array_to_Collection() {

            var source = TypeDescriptor.valueOf(Object[].class);
            var unknownTarget = TypeDescriptor.valueOf(Collection.class);
            var setTarget = TypeDescriptor.collection(
                Set.class,
                TypeDescriptor.valueOf(BigDecimal.class)
            );
            var listTarget = TypeDescriptor.collection(
                ArrayList.class,
                TypeDescriptor.valueOf(BigDecimal.class)
            );

            var output = oracleConverters.convert(new Object[0], source, unknownTarget);
            assertThat(output).isEqualTo(Collections.emptyList());

            output = oracleConverters.convert(new Object[]{1, 2}, source, unknownTarget);
            assertThat(output).isEqualTo(List.of(1, 2));

            output = oracleConverters.convert(new Object[0], source, setTarget);
            assertThat(output).isEqualTo(Collections.emptySet());

            output = oracleConverters.convert(new Object[]{1, 2}, source, setTarget);
            assertThat(output).isEqualTo(Set.of(1, 2));

            output = oracleConverters.convert(new Object[0], source, listTarget);
            assertThat(output).isEqualTo(Collections.emptyList());

            output = oracleConverters.convert(new Object[]{1, 2}, source, listTarget);
            assertThat(output).isEqualTo(List.of(1, 2));

            return_null_if_input_is_null(source, unknownTarget);
            return_null_if_input_is_null(source, setTarget);
            return_null_if_input_is_null(source, listTarget);
        }

        @Test
        void convert_Collection_to_Collection() {

            var source = TypeDescriptor.valueOf(Collection.class);
            var unknownTarget = TypeDescriptor.valueOf(Collection.class);
            var listTarget = TypeDescriptor.collection(
                ArrayList.class,
                TypeDescriptor.valueOf(Integer.class)
            );

            var output = oracleConverters.convert(Collections.emptyList(), source, unknownTarget);
            assertThat(output).isEqualTo(Collections.emptySet());

            output = oracleConverters.convert(List.of(1, 2), source, unknownTarget);
            assertThat(output).isEqualTo(Set.of(1, 2));

            output = oracleConverters.convert(List.of(1, 2), source, listTarget);
            assertThat(output).isEqualTo(List.of(1, 2));

            return_null_if_input_is_null(source, unknownTarget);
            return_null_if_input_is_null(source, listTarget);
        }

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