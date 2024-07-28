package io.spring.jdbc.oracle.converter.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.spring.jdbc.oracle.converter.ConvertKey;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverters;
import io.spring.jdbc.oracle.exception.ValueException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DefaultOracleConvertersTest {

    static final OracleConverters oracleConverters = DefaultOracleConverters.INSTANCE;

    void return_null_if_input_is_null(TypeDescriptor sourceType, TypeDescriptor targetType) {

        assertThat(oracleConverters.convert(null, sourceType, targetType))
            .isNull();
    }

    @Test
    void throw_exception_when_adding_null_converter() {

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> oracleConverters.addGenericConverter(null));

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> oracleConverters.addConverter(null));

        var nullConvertKey = new NullConvertKey();
        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> oracleConverters.addConverter(nullConvertKey));

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> oracleConverters.addGenericConverter(nullConvertKey));
    }

    @Test
    void return_null_when_type_is_null() {

        assertThat(oracleConverters.convert(null, null, TypeDescriptor.valueOf(Object.class)))
            .isNull();

        assertThat(oracleConverters.convert(null, TypeDescriptor.valueOf(Object.class), null))
            .isNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    void do_nothing_when_adding_replicate_converter() {

        oracleConverters.addGenericConverter(new ClonedNumberToString());
        oracleConverters.addConverter(new ClonedLocalDatetimeToTimestampOracleConverter());

        var converterCachesField = ReflectionUtils.findField(
            DefaultOracleConverters.class,
            "converterCaches"
        );
        ReflectionUtils.makeAccessible(converterCachesField);
        var converterCaches =
            (Map<ConvertKey, Object>) ReflectionUtils.getField(
                converterCachesField,
                oracleConverters
            );
        assertThat(converterCaches)
            .hasSize(2);

        var genericConvertersField = ReflectionUtils.findField(
            DefaultOracleConverters.class,
            "genericConverters"
        );
        ReflectionUtils.makeAccessible(genericConvertersField);
        var genericConverters = (Set<Object>) ReflectionUtils.getField(
            genericConvertersField,
            oracleConverters
        );
        assertThat(genericConverters)
            .hasSize(5);
    }

    record NullConvertKey() implements GenericOracleConverter {

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

            return true;
        }

        @Override
        public ConvertKey getConvertKey() {

            return null;
        }

        @Override
        public Object convert(Object source) {

            return null;
        }

    }

    record ClonedLocalDatetimeToTimestampOracleConverter() implements
        OracleConverter<LocalDateTime, Timestamp> {

        @Override
        public Timestamp convert(LocalDateTime source) {

            return Optional.ofNullable(source)
                .map(Timestamp::valueOf)
                .orElse(null);
        }

    }

    record ClonedNumberToString() implements GenericOracleConverter {

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

            return Number.class.isAssignableFrom(sourceType.getType())
                && String.class.isAssignableFrom(targetType.getType());
        }

        @Override
        public ConvertKey getConvertKey() {

            return new ConvertKey(Number.class, String.class);
        }

        @Override
        public Object convert(Object source) {

            return Optional.ofNullable((Number) source)
                .map(Number::toString)
                .orElse(null);
        }

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

        @Test
        void convert_Array_to_Array() {

            var source = TypeDescriptor.valueOf(int[].class);
            var target = TypeDescriptor.valueOf(String[].class);

            var output = oracleConverters.convert(new Object[0], source, target);
            assertThat(output).isEqualTo(new Object[0]);

            output = oracleConverters.convert(new int[]{1, 2}, source, target);
            assertThat(output).isEqualTo(new String[]{"1", "2"});

            return_null_if_input_is_null(source, target);
        }

        @Test
        void convert_Collection_to_Array() {

            var source = TypeDescriptor.valueOf(Collection.class);
            var target = TypeDescriptor.valueOf(int[].class);

            var output = oracleConverters.convert(Collections.emptyList(), source, target);
            assertThat(output).isEqualTo(new int[0]);

            output = oracleConverters.convert(List.of(1, 2), source, target);
            assertThat(output).isEqualTo(new int[]{1, 2});

            return_null_if_input_is_null(source, target);
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