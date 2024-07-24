package io.spring.jdbc.oracle.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.converter.support.NoneConverter;
import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.property.TypeProperty;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MapperUtilsTest {

    @Nested
    class ConvertValue {

        @Test
        void with_null_param() {

            assertThat(MapperUtils.convertValue(null, BigDecimal.ONE))
                .isNull();

            assertThat(MapperUtils.convertValue(new TypeProperty(), null))
                .isNull();

            var typeProperty = new TypeProperty();
            typeProperty.setConverter(NoneConverter.class);
            assertThatExceptionOfType(ValueException.class)
                .isThrownBy(() -> MapperUtils.convertValue(typeProperty, BigDecimal.ONE));
        }

        @Test
        void convert_object_to_object() {

            var typeProperty = new TypeProperty();
            typeProperty.setConverter(BigDecimalToIntegerConverter.class);
            assertThat(MapperUtils.convertValue(typeProperty, BigDecimal.ONE))
                .isEqualTo(1);
        }

        public static class BigDecimalToIntegerConverter implements
            OracleConverter<BigDecimal, Integer> {

            @Override
            public Integer convert(BigDecimal source) {

                return source.intValue();
            }

        }

    }

}