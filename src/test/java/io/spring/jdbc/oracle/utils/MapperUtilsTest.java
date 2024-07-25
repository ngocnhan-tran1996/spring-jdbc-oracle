package io.spring.jdbc.oracle.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.converter.support.NoneConverter;
import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.property.TypeProperty;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MapperUtilsTest {

    @Nested
    class ToArrayOrNull {

        @Test
        void return_null_when_input_is_null() {

            assertThat(MapperUtils.toArrayOrNull(null))
                .isNull();
        }

        @Test
        void throw_exception_when_input_is_neither_array_nor_collection() {

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> MapperUtils.toArrayOrNull("null"));

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> MapperUtils.toArrayOrNull(Integer.class));

            var emptyMap = Collections.emptyMap();
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> MapperUtils.toArrayOrNull(emptyMap));
        }

        @Test
        void return_object_array() {

            assertThat(MapperUtils.toArrayOrNull(Collections.emptyList()))
                .isEmpty();

            assertThat(MapperUtils.toArrayOrNull(new String[0]))
                .isEmpty();

            assertThat(MapperUtils.toArrayOrNull(List.of(1, "X")))
                .isNotEmpty()
                .containsExactly(1, "X");
        }

    }

    @Nested
    class ExtractClassFromArray {

        @Test
        void throw_exception_when_input_is_either_null_or_not_array_type() {

            assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> MapperUtils.extractClassFromArray(null));

            var classTypeDescriptor = TypeDescriptor.valueOf(null);
            assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> MapperUtils.extractClassFromArray(classTypeDescriptor));

            var bigDecimalTypeDescriptor = TypeDescriptor.forObject(BigDecimal.TEN);
            assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> MapperUtils.extractClassFromArray(bigDecimalTypeDescriptor));
        }

        @Test
        void return_type_class() {

            var array = TypeDescriptor.array(TypeDescriptor.valueOf(BigDecimal.class));
            assertThat(MapperUtils.extractClassFromArray(array))
                .isEqualTo(BigDecimal.class);

            assertThat(MapperUtils.extractClassFromArray(TypeDescriptor.forObject(new Integer[0])))
                .isEqualTo(Integer.class);

            var collection = TypeDescriptor.collection(
                List.class,
                TypeDescriptor.valueOf(String.class)
            );
            assertThat(MapperUtils.extractClassFromArray(collection))
                .isEqualTo(String.class);

            assertThat(MapperUtils.extractClassFromArray(TypeDescriptor.forObject(List.of())))
                .isNull();
        }

    }

    @Nested
    class ConvertValue {

        @Test
        void return_null_when_method_has_null_value_parameter() {

            assertThat(MapperUtils.convertValue(null, "X"))
                .isNull();

            assertThat(MapperUtils.convertValue(new TypeProperty(), null))
                .isNull();
        }

        @Test
        void throw_exception_when_method_convert_is_not_found() {

            var typeProperty = new TypeProperty();
            typeProperty.setConverter(NoneConverter.class);

            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> MapperUtils.convertValue(typeProperty, BigDecimal.ONE));
        }

        @Test
        void convert_bigDecimal_to_integer() {

            var typeProperty = new TypeProperty();
            typeProperty.setConverter(BigDecimalToInteger.class);

            assertThat(MapperUtils.convertValue(typeProperty, BigDecimal.ONE))
                .isEqualTo(1);

            assertThat(MapperUtils.convertValue(typeProperty, null))
                .isNull();
        }

        public record BigDecimalToInteger() implements OracleConverter<BigDecimal, Integer> {

            @Override
            public Integer convert(BigDecimal source) {

                return Optional.of(source)
                    .map(BigDecimal::intValue)
                    .orElse(null);
            }

        }

    }

}