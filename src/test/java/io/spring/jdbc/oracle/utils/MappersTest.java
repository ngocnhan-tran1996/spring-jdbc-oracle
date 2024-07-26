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
class MappersTest {

    @Nested
    class ToArray {

        @Test
        void return_null_when_input_is_null() {

            assertThat(Mappers.toArray(null))
                .isNull();
        }

        @Test
        void throw_exception_when_input_is_neither_array_nor_collection() {

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Mappers.toArray("null"));

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Mappers.toArray(Integer.class));

            var emptyMap = Collections.emptyMap();
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Mappers.toArray(emptyMap));
        }

        @Test
        void return_object_array() {

            assertThat(Mappers.toArray(Collections.emptyList()))
                .isEqualTo(new Object[0]);

            assertThat(Mappers.toArray(new String[0]))
                .isEqualTo(new Object[0]);

            assertThat(Mappers.toArray(List.of(1, "X")))
                .isEqualTo(new Object[]{1, "X"});
        }

    }

    @Nested
    class ExtractClassFromArray {

        @Test
        void throw_exception_when_input_is_either_null_or_not_array_type() {

            assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Mappers.extractClassFromArray(null));

            var classTypeDescriptor = TypeDescriptor.valueOf(null);
            assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> Mappers.extractClassFromArray(classTypeDescriptor));

            var bigDecimalTypeDescriptor = TypeDescriptor.forObject(BigDecimal.TEN);
            assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> Mappers.extractClassFromArray(bigDecimalTypeDescriptor));
        }

        @Test
        void return_null_when_type_class_is_not_determined() {

            assertThat(Mappers.extractClassFromArray(TypeDescriptor.forObject(List.of())))
                .isNull();
        }

        @Test
        void return_type_class() {

            var array = TypeDescriptor.array(TypeDescriptor.valueOf(BigDecimal.class));
            assertThat(Mappers.extractClassFromArray(array))
                .isEqualTo(BigDecimal.class);

            assertThat(Mappers.extractClassFromArray(TypeDescriptor.forObject(new Integer[0])))
                .isEqualTo(Integer.class);

            var collection = TypeDescriptor.collection(
                List.class,
                TypeDescriptor.valueOf(String.class)
            );
            assertThat(Mappers.extractClassFromArray(collection))
                .isEqualTo(String.class);

            assertThat(Mappers.extractClassFromArray(TypeDescriptor.forObject(List.of())))
                .isNull();
        }

    }

    @Nested
    class ConvertValue {

        @Test
        void return_null_when_method_has_null_value_parameter() {

            assertThat(Mappers.convertValue(null, "X"))
                .isNull();

            assertThat(Mappers.convertValue(new TypeProperty(), null))
                .isNull();
        }

        @Test
        void throw_exception_when_method_convert_is_not_found() {

            var typeProperty = new TypeProperty();
            typeProperty.setConverter(NoneConverter.class);

            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> Mappers.convertValue(typeProperty, BigDecimal.ONE));
        }

        @Test
        void convert_BigDecimal_to_integer() {

            var typeProperty = new TypeProperty();
            typeProperty.setConverter(BigDecimalToInteger.class);

            assertThat(Mappers.convertValue(typeProperty, BigDecimal.ONE))
                .isEqualTo(1);
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